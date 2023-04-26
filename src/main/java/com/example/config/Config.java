package com.example.config;

import org.axonframework.eventsourcing.eventstore.EmbeddedEventStore;
import org.axonframework.eventsourcing.eventstore.EventStore;
import org.axonframework.eventsourcing.eventstore.inmemory.InMemoryEventStorageEngine;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Config {
    @Bean
    public EventStore eventStore() {
        return EmbeddedEventStore.builder()
                .storageEngine(new InMemoryEventStorageEngine())
                .build();
    }
}
