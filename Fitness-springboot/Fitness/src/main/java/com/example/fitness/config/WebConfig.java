package com.example.fitness.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String physicalPath = Paths.get(System.getProperty("user.dir"), "uploads").toAbsolutePath().toString();
        String uploadDir = "file:" + (physicalPath.endsWith("/") || physicalPath.endsWith("\\") ? physicalPath : physicalPath + "/");
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(uploadDir);
    }
}
