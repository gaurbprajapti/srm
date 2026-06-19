package com.srm.campusnexus.config;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Alternative Global CORS Configuration
 * 
 * This class provides multiple ways to configure CORS:
 * 1. WebMvcConfigurer approach (for MVC controllers)
 * 2. CorsFilter Bean approach (global filter)
 * 
 * Note: Choose ONE approach - either this OR the SecurityConfig CORS
 * Currently using SecurityConfig CORS, so this is provided as reference
 */
// @Configuration // Uncomment to enable this configuration
public class CorsConfig implements WebMvcConfigurer {

    @Value("${cors.allowed-origins}")
    private String allowedOrigins;

    @Value("${cors.allowed-methods}")
    private String allowedMethods;

    @Value("${cors.allowed-headers}")
    private String allowedHeaders;

    @Value("${cors.exposed-headers}")
    private String exposedHeaders;

    @Value("${cors.allow-credentials}")
    private boolean allowCredentials;

    @Value("${cors.max-age}")
    private long maxAge;

    /**
     * Method 1: WebMvcConfigurer approach
     * Good for MVC controllers, but doesn't work with Spring Security endpoints
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        List<String> origins = Arrays.asList(allowedOrigins.split(","));
        List<String> methods = Arrays.asList(allowedMethods.split(","));
        List<String> headers = Arrays.asList(allowedHeaders.split(","));
        List<String> exposed = Arrays.asList(exposedHeaders.split(","));

        registry.addMapping("/api/**")
                .allowedOriginPatterns(origins.toArray(new String[0]))
                .allowedMethods(methods.toArray(new String[0]))
                .allowedHeaders(headers.toArray(new String[0]))
                .exposedHeaders(exposed.toArray(new String[0]))
                .allowCredentials(allowCredentials)
                .maxAge(maxAge);
    }

    /**
     * Method 2: Global CorsFilter Bean
     * Works globally for all endpoints including Spring Security
     */
    // @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Parse and set allowed origins
        List<String> origins = Arrays.asList(allowedOrigins.split(","));
        configuration.setAllowedOriginPatterns(origins);

        // Parse and set allowed methods
        List<String> methods = Arrays.asList(allowedMethods.split(","));
        configuration.setAllowedMethods(methods);

        // Parse and set allowed headers
        List<String> headers = Arrays.asList(allowedHeaders.split(","));
        configuration.setAllowedHeaders(headers);

        // Parse and set exposed headers
        List<String> exposed = Arrays.asList(exposedHeaders.split(","));
        configuration.setExposedHeaders(exposed);

        configuration.setAllowCredentials(allowCredentials);
        configuration.setMaxAge(maxAge);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return new CorsFilter(source);
    }
}