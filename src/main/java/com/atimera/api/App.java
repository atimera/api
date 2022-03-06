package com.atimera.api;

import com.atimera.api.domain.Server;
import com.atimera.api.enumeration.Status;
import com.atimera.api.repositories.ServerRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

import static com.atimera.api.constant.FileConstant.USER_FOLDER;

@SpringBootApplication
@Slf4j
public class App {

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);

        if (new File(USER_FOLDER).mkdirs()) {
            log.info("Le dossier utilisateur a été créé sur {}", USER_FOLDER);
        } else if (!Files.exists(Path.of(USER_FOLDER))) {
            log.warn("On n'a pas pu créer le dossier utilisateur: {}", USER_FOLDER);
        }
    }

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowCredentials(true);
        corsConfiguration.setAllowedOrigins(Arrays.asList("http://localhost:3000", "http://localhost:4200"));
        corsConfiguration.setAllowedHeaders(Arrays.asList("Origin", "Access-Control-Allow-Origin", "Content-Type",
                "Accept", "Jwt-Token", "Authorization", "Origin, Accept", "X-Requested-With",
                "Access-Control-Request-Method", "Access-Control-Request-Headers"));
        corsConfiguration.setExposedHeaders(Arrays.asList("Origin", "Content-Type",
                "Accept", "Jwt-Token", "Authorization", "Filename",
                "Access-Control-Allow-Origin", "Access-Control-Allow-Credentials"
        ));
        corsConfiguration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        UrlBasedCorsConfigurationSource urlBasedCorsConfigurationSource = new UrlBasedCorsConfigurationSource();
        urlBasedCorsConfigurationSource.registerCorsConfiguration("/**", corsConfiguration);
        return new CorsFilter(urlBasedCorsConfigurationSource);
    }

    //@Bean
    CommandLineRunner run(ServerRepository serverRepository) {
        return args -> {
            serverRepository.save(new Server(null, "192.168.0.50", "Ubuntu Linux", "16 GB", "Personal PC",
                    "http://localhost:8080/server/image/server1.jpg", Status.SERVER_DOWN));
            serverRepository.save(new Server(null, "192.168.0.52", "Windows 10 Server", "100 GB", "RATP PC",
                    "http://localhost:8080/server/image/server2.jpg", Status.SERVER_UP));
        };
    }

}
