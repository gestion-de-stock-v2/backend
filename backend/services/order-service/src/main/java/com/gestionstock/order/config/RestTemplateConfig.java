package com.gestionstock.order.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {

    /**
     * {@code @LoadBalanced} permet d'adresser les services par leur nom Eureka
     * (ex. {@code http://stock-service/...}) plutot que par une URL fixe pointant sur la
     * passerelle. Le trafic interne ne transite donc plus par le gateway, qui n'est plus
     * ni un point de passage unique ni un goulot d'etranglement pour les appels internes.
     */
    @Bean
    @LoadBalanced
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
