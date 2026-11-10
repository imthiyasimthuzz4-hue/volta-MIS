package in.volta.mis.config;

import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.net.URI;

@Configuration
public class DataSourceConfig {

    private static final Logger log = LoggerFactory.getLogger(DataSourceConfig.class);

    @Value("${spring.datasource.url:}")
    private String rawUrl;

    @Value("${spring.datasource.username:}")
    private String username;

    @Value("${spring.datasource.password:}")
    private String password;

    @Bean
    @Primary
    public DataSource dataSource() {
        HikariDataSource ds = new HikariDataSource();

        // 1. Check for Cloud PostgreSQL URL format (Render, Vercel Postgres, Supabase, Neon, Railway)
        String envDbUrl = System.getenv("DATABASE_URL");
        if (envDbUrl == null || envDbUrl.isBlank()) {
            envDbUrl = System.getenv("DB_URL");
        }
        if (envDbUrl == null || envDbUrl.isBlank()) {
            envDbUrl = System.getenv("POSTGRES_URL");
        }
        if (envDbUrl == null || envDbUrl.isBlank()) {
            envDbUrl = System.getenv("POSTGRES_PRISMA_URL");
        }
        String effectiveUrl = (envDbUrl != null && !envDbUrl.isBlank()) ? envDbUrl : rawUrl;

        if (effectiveUrl != null && (effectiveUrl.startsWith("postgres://") || effectiveUrl.startsWith("postgresql://"))) {
            try {
                log.info("Detected Cloud PostgreSQL URL format from Cloud Provider (Vercel/Render). Converting to JDBC...");
                URI uri = new URI(effectiveUrl.replace("postgres://", "postgresql://"));
                String host = uri.getHost();
                int port = uri.getPort() == -1 ? 5432 : uri.getPort();
                String path = uri.getPath(); // e.g. /volta_mis
                String userInfo = uri.getUserInfo();

                String dbUser = username;
                String dbPass = password;

                if (userInfo != null && userInfo.contains(":")) {
                    String[] parts = userInfo.split(":", 2);
                    dbUser = parts[0];
                    dbPass = parts[1];
                }

                String jdbcUrl = "jdbc:postgresql://" + host + ":" + port + path;
                if (!jdbcUrl.contains("?")) {
                    jdbcUrl += "?sslmode=prefer";
                }

                log.info("Connecting to Cloud PostgreSQL: jdbc:postgresql://{}:{}{}", host, port, path);

                ds.setJdbcUrl(jdbcUrl);
                ds.setDriverClassName("org.postgresql.Driver");
                ds.setUsername(dbUser);
                ds.setPassword(dbPass);
                ds.setMaximumPoolSize(5); // Optimized for Cloud Free Tier (low RAM usage)
                ds.setMinimumIdle(1);
                ds.setIdleTimeout(30000);
                ds.setMaxLifetime(60000);
                return ds;
            } catch (Exception e) {
                log.error("Failed to parse Cloud PostgreSQL URI, falling back to raw URL: {}", e.getMessage());
            }
        }

        // 2. Check if running on Render / Vercel / Cloud without external DB specified -> Graceful H2 Fallback
        boolean isCloud = System.getenv("RENDER") != null || System.getenv("VERCEL") != null;
        if (isCloud && (effectiveUrl == null || effectiveUrl.contains("localhost:3306"))) {
            log.info("Running on Cloud (Render/Vercel) without external DB specified. Using persistent embedded H2 file database.");
            ds.setJdbcUrl("jdbc:h2:file:/app/uploads/volta_db;DB_CLOSE_DELAY=-1;MODE=MySQL");
            ds.setDriverClassName("org.h2.Driver");
            ds.setUsername("sa");
            ds.setPassword("");
            ds.setMaximumPoolSize(5);
            return ds;
        }

        // 3. Standard JDBC connection (Local MySQL, H2, or custom JDBC)
        ds.setJdbcUrl(effectiveUrl != null ? effectiveUrl : rawUrl);
        ds.setUsername(username);
        ds.setPassword(password);
        if (effectiveUrl != null && effectiveUrl.contains("postgresql")) {
            ds.setDriverClassName("org.postgresql.Driver");
        } else if (effectiveUrl != null && effectiveUrl.contains("h2")) {
            ds.setDriverClassName("org.h2.Driver");
        }
        return ds;
    }
}
