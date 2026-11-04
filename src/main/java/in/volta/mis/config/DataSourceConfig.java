package in.volta.mis.config;

import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
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

        if (rawUrl != null && (rawUrl.startsWith("postgres://") || rawUrl.startsWith("postgresql://"))) {
            try {
                log.info("Detected Cloud PostgreSQL URL format. Converting to JDBC...");
                URI uri = new URI(rawUrl.replace("postgres://", "postgresql://"));
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
                log.info("Constructed Cloud JDBC URL: jdbc:postgresql://{}:{}{}", host, port, path);

                ds.setJdbcUrl(jdbcUrl);
                ds.setDriverClassName("org.postgresql.Driver");
                ds.setUsername(dbUser);
                ds.setPassword(dbPass);
                ds.setMaximumPoolSize(5); // Optimized for Render Free Tier (low memory)
                ds.setMinimumIdle(1);
                ds.setIdleTimeout(30000);
                ds.setMaxLifetime(60000);
                return ds;
            } catch (Exception e) {
                log.error("Failed to parse Cloud PostgreSQL URI, falling back to raw URL: {}", e.getMessage());
            }
        }

        // Standard JDBC connection (MySQL, H2, or already formatted jdbc:postgresql)
        ds.setJdbcUrl(rawUrl);
        ds.setUsername(username);
        ds.setPassword(password);
        if (rawUrl != null && rawUrl.contains("postgresql")) {
            ds.setDriverClassName("org.postgresql.Driver");
        }
        return ds;
    }
}
