package com.fullcycle.catalogo.infrastructure.category;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;

import com.fullcycle.catalogo.AbstractRestClientTest;
import com.fullcycle.catalogo.domain.Fixture;
import com.fullcycle.catalogo.domain.exceptions.InternalErrorException;
import com.fullcycle.catalogo.infrastructure.category.models.CategoryDTO;
import io.github.resilience4j.bulkhead.BulkheadFullException;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

public class CategoryRestClientTest extends AbstractRestClientTest {

  @Autowired
  private CategoryRestClient target;

  @Test
  public void givenACategory_whenReceive200FromServer_shouldBeOk() {
    // given
    final var aulas = Fixture.Categories.aulas();

    final var responseBody = writeValueAsString(new CategoryDTO(
        aulas.id(),
        aulas.name(),
        aulas.description(),
        aulas.active(),
        aulas.createdAt(),
        aulas.updatedAt(),
        aulas.deletedAt()
    ));

    stubFor(
        get(urlPathEqualTo("/api/categories/%s".formatted(aulas.id())))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .withBody(responseBody)
            )
    );

    // when
    final var actualCategory = target.getById(aulas.id()).get();

    // then
    Assertions.assertEquals(aulas.id(), actualCategory.id());
    Assertions.assertEquals(aulas.name(), actualCategory.name());
    Assertions.assertEquals(aulas.description(), actualCategory.description());
    Assertions.assertEquals(aulas.active(), actualCategory.active());
    Assertions.assertEquals(aulas.createdAt(), actualCategory.createdAt());
    Assertions.assertEquals(aulas.updatedAt(), actualCategory.updatedAt());
    Assertions.assertEquals(aulas.deletedAt(), actualCategory.deletedAt());

    verify(1, getRequestedFor(urlPathEqualTo("/api/categories/%s".formatted(aulas.id()))));
  }

  @Test
  public void givenACategory_whenReceive5xxFromServer_shouldReturnInternalError() {
    // given
    final var expectedId = "123";
    final var expectedErrorMessage = "Error observed from categories [resourceId:%s] [status:500]".formatted(
        expectedId);

    final var responseBody = writeValueAsString(
        Map.of("message", "Internal Server Error"));

    stubFor(
        get(urlPathEqualTo("/api/categories/%s".formatted(expectedId)))
            .willReturn(aResponse()
                .withStatus(500)
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .withBody(responseBody)
            )
    );

    // when
    final var actualEx = Assertions.assertThrows(InternalErrorException.class,
        () -> target.getById(expectedId));

    // then
    Assertions.assertEquals(expectedErrorMessage, actualEx.getMessage());

    verify(2, getRequestedFor(urlPathEqualTo("/api/categories/%s".formatted(expectedId))));
  }

  @Test
  public void givenACategory_whenReceive404NotFoundFromServer_shouldReturnEmpty() {
    // given
    final var expectedId = "123";
    final var responseBody = writeValueAsString(Map.of("message", "Not found"));

    stubFor(
        get(urlPathEqualTo("/api/categories/%s".formatted(expectedId)))
            .willReturn(aResponse()
                .withStatus(404)
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .withBody(responseBody)
            )
    );

    // when
    final var actualCategory = target.getById(expectedId);

    // then
    Assertions.assertTrue(actualCategory.isEmpty());

    verify(1, getRequestedFor(urlPathEqualTo("/api/categories/%s".formatted(expectedId))));
  }

  @Test
  public void givenACategory_whenReceiveTimeout_shouldReturnInternalError() {
    // given
    final var aulas = Fixture.Categories.aulas();
    final var expectedErrorMessage = "Timeout observed from categories [resourceId:%s]".formatted(
        aulas.id());

    final var responseBody = writeValueAsString(new CategoryDTO(
        aulas.id(),
        aulas.name(),
        aulas.description(),
        aulas.active(),
        aulas.createdAt(),
        aulas.updatedAt(),
        aulas.deletedAt()
    ));

    stubFor(
        get(urlPathEqualTo("/api/categories/%s".formatted(aulas.id())))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .withFixedDelay(1000)
                .withBody(responseBody)
            )
    );

    // when
    final var actualEx = Assertions.assertThrows(InternalErrorException.class,
        () -> target.getById(aulas.id()));

    // then
    Assertions.assertEquals(expectedErrorMessage, actualEx.getMessage());

    verify(2, getRequestedFor(urlPathEqualTo("/api/categories/%s".formatted(aulas.id()))));
  }

  @Test
  public void givenACategory_whenBulkheadIsFull_shouldReturnError() {
    // given
    final var expectedErrorMessage = "Bulkhead 'categories' is full and does not permit further calls";

    acquireBulkheadPermission(CATEGORY);

    // when
    final var actualEx = Assertions.assertThrows(BulkheadFullException.class, () -> target.getById("123"));

    // then
    Assertions.assertEquals(expectedErrorMessage, actualEx.getMessage());

    releaseBulkheadPermission(CATEGORY);
  }
}
