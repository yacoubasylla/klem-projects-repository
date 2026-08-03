package com.klem.cantine.common;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Sert les fichiers uploadés (ex. certificats médicaux) sous /uploads/**.
 * Noms de fichiers en UUID (non énumérables) — voir FileStorageService.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${storage.uploads-dir:uploads}")
    private String uploadsDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String location = "file:" + uploadsDir + (uploadsDir.endsWith("/") ? "" : "/");
        registry.addResourceHandler("/uploads/**").addResourceLocations(location);
    }
}
