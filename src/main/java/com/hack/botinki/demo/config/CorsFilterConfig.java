// src/main/java/com/hack/botinki/demo/config/CorsFilterConfig.java

package com.hack.botinki.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsFilterConfig {

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();

        // РАЗРЕШАЕМ VERСEL
        config.addAllowedOriginPattern("https://hack-front-v1-*-levchenkovalexanders-projects.vercel.app");
        config.addAllowedOrigin("https://hack-front-v1.vercel.app/");
        config.addAllowedOriginPattern("http://localhost:3000");

        // РАЗРЕШАЕМ ВСЁ
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        config.setAllowCredentials(false);

        // ВАЖНО: /** — для ВСЕХ путей
        source.registerCorsConfiguration("/**", config);

        return new CorsFilter(source);
    }
}