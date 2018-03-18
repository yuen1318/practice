package io.toro.pairprogramming.config;

import io.toro.pairprogramming.services.storages.ProjectStorageService;
import io.toro.pairprogramming.services.storages.LocalProjectStorage;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StorageConfig {

    @Bean
    public ProjectStorageService storageService() {
        return new LocalProjectStorage();
    }
}
