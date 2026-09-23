package com.gestionstock.auth.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaAuthTopicConfig {

    @Bean
    public NewTopic passwordResetTopic() {
        return TopicBuilder.name("password-reset-topic").build();
    }
}
