package com.fullcycle.catalogo.infrastructure.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fullcycle.catalogo.infrastructure.configuration.annotations.Categories;
import com.fullcycle.catalogo.infrastructure.configuration.annotations.Genres;
import com.fullcycle.catalogo.infrastructure.configuration.annotations.Keycloak;
import com.fullcycle.catalogo.infrastructure.configuration.annotations.Videos;
import com.fullcycle.catalogo.infrastructure.configuration.properties.RestClientProperties;
import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestClient;

@Configuration(proxyBeanMethods = false)
public class RestClientConfig {

  @Bean
  @Categories
  @ConfigurationProperties(prefix = "rest-client.categories")
  public RestClientProperties categoryRestClientProperties() {
    return new RestClientProperties();
  }

  @Bean
  @Genres
  @ConfigurationProperties(prefix = "rest-client.genres")
  public RestClientProperties genreRestClientProperties() {
    return new RestClientProperties();
  }

  @Bean
  @Videos
  @ConfigurationProperties(prefix = "rest-client.videos")
  public RestClientProperties videosRestClientProperties() {
    return new RestClientProperties();
  }

  @Bean
  @Keycloak
  @ConfigurationProperties(prefix = "rest-client.keycloak")
  public RestClientProperties keycloakRestClientProperties() {
    return new RestClientProperties();
  }

  @Bean
  @Categories
  public RestClient categoryHttpClient(@Categories final RestClientProperties properties, final ObjectMapper objectMapper) {
    return restClient(properties, objectMapper);
  }

  @Bean
  @Genres
  public RestClient genreHttpClient(@Genres final RestClientProperties properties, final ObjectMapper objectMapper) {
    return restClient(properties, objectMapper);
  }

  @Bean
  @Videos
  public RestClient videoHttpClient(@Videos final RestClientProperties properties, final ObjectMapper objectMapper) {
    return restClient(properties, objectMapper);
  }

  @Bean
  @Keycloak
  public RestClient keycloakHttpClient(@Keycloak final RestClientProperties properties, final ObjectMapper objectMapper) {
    return restClient(properties, objectMapper);
  }

  private static RestClient restClient(final RestClientProperties properties, final ObjectMapper objectMapper) {
    final var factory = new JdkClientHttpRequestFactory();
    factory.setReadTimeout(properties.readTimeout());

    return RestClient.builder()
        .baseUrl(properties.baseUrl())
        .requestFactory(factory)
        .messageConverters(converters -> {
          converters.removeIf(it -> it instanceof MappingJackson2HttpMessageConverter);
          converters.add(jsonConverter(objectMapper));
          converters.add(new FormHttpMessageConverter());
        })
        .build();
  }

  private static MappingJackson2HttpMessageConverter jsonConverter(ObjectMapper objectMapper) {
    final var jsonConverter = new MappingJackson2HttpMessageConverter(objectMapper);
    jsonConverter.setSupportedMediaTypes(List.of(MediaType.APPLICATION_JSON));
    return jsonConverter;
  }

}
