package com.fullcycle.catalogo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fullcycle.catalogo.infrastructure.category.CategoryRestClient;
import com.fullcycle.catalogo.infrastructure.configuration.WebServerConfig;
import com.github.tomakehurst.wiremock.client.WireMock;
import io.github.resilience4j.bulkhead.BulkheadRegistry;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test-integration")
@AutoConfigureWireMock(port = 0)
@EnableAutoConfiguration(exclude = {
    ElasticsearchRepositoriesAutoConfiguration.class,
    KafkaAutoConfiguration.class,
})
@SpringBootTest(classes = {WebServerConfig.class, IntegrationTestConfiguration.class})
@Tag("integrationTest")
public abstract class AbstractRestClientTest {

  protected static final String CATEGORY = CategoryRestClient.NAMESPACE;
  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private BulkheadRegistry bulkheadRegistry;

  @Autowired
  private CircuitBreakerRegistry circuitBreakerRegistry;

  @BeforeEach
  public void beforeEach(){
    WireMock.reset();
    WireMock.resetAllRequests();
    List.of(CATEGORY).forEach(this::resetFaultTolerance);
  }

  protected String writeValueAsString(Object obj){
    try {
      return objectMapper.writeValueAsString(obj);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }

  protected void acquireBulkheadPermission(final String name) {
    bulkheadRegistry.bulkhead(name).acquirePermission();
  }

  protected void releaseBulkheadPermission(final String name) {
    bulkheadRegistry.bulkhead(name).releasePermission();
  }

  protected void checkCircuitBreakerState(final String name, final CircuitBreaker.State expectedState) {
    final var cb = circuitBreakerRegistry.circuitBreaker(name);
    Assertions.assertEquals(expectedState, cb.getState());
  }

  protected void transitionToOpenState(final String name) {
    circuitBreakerRegistry.circuitBreaker(name).transitionToOpenState();
  }

  protected void transitionToClosedState(final String name) {
    circuitBreakerRegistry.circuitBreaker(name).transitionToClosedState();
  }

  private void resetFaultTolerance(String name) {
    circuitBreakerRegistry.circuitBreaker(name).reset();
  }
}
