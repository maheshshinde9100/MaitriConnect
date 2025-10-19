package com.maitriconnect.auth_service.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {
    
    @Bean
    public NewTopic userRegisteredTopic() {
        return TopicBuilder.name("user.registered")
                .partitions(3)
                .replicas(1)
                .build();
    }
    
    @Bean
    public NewTopic userLoggedInTopic() {
        return TopicBuilder.name("user.logged_in")
                .partitions(3)
                .replicas(1)
                .build();
    }
    
    @Bean
    public NewTopic userUpdatedTopic() {
        return TopicBuilder.name("user.updated")
                .partitions(3)
                .replicas(1)
                .build();
    }
    
    @Bean
    public NewTopic userDeletedTopic() {
        return TopicBuilder.name("user.deleted")
                .partitions(3)
                .replicas(1)
                .build();
    }
}
