package com.fullcycle.catalogo.infrastructure.kafka;

import com.fullcycle.catalogo.AbstractEmbeddedKafkaTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class CategoryListenerTest extends AbstractEmbeddedKafkaTest {

    @Test
    public void testDummy(){
        Assertions.assertNotNull(producer());
    }
}
