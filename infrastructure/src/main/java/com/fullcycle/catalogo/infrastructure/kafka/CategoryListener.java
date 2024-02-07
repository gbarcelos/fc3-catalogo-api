package com.fullcycle.catalogo.infrastructure.kafka;

import org.springframework.kafka.annotation.KafkaListener;

public class CategoryListener {


    @KafkaListener(
            id = "${kafka.consumers.categories.id}",
            topics = "${kafka.consumers.categories.topics}"
    )
    public void mock(){

    }
}
