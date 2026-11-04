package in.volta.mis.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Value("${volta.upload.dir:uploads}")
    private String uploadDirSetting;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String baseDir = (uploadDirSetting != null && !uploadDirSetting.isBlank()) ? uploadDirSetting : "uploads";
        Path uploadDir = Paths.get(baseDir);
        String uploadUri = uploadDir.toUri().toString();
        if (!uploadUri.endsWith("/")) {
            uploadUri += "/";
        }
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(uploadUri);
    }
}
