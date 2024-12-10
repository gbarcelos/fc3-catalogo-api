package com.fullcycle.catalogo.infrastructure.graphql;

import static com.fullcycle.catalogo.infrastructure.configuration.security.Roles.ROLE_ADMIN;
import static com.fullcycle.catalogo.infrastructure.configuration.security.Roles.ROLE_GENRES;
import static com.fullcycle.catalogo.infrastructure.configuration.security.Roles.ROLE_SUBSCRIBER;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.fullcycle.catalogo.IntegrationTest;
import com.fullcycle.catalogo.WebGraphQlSecurityInterceptor;
import com.fullcycle.catalogo.application.genre.list.ListGenreUseCase;
import com.fullcycle.catalogo.application.genre.save.SaveGenreUseCase;
import com.fullcycle.catalogo.domain.Fixture;
import com.fullcycle.catalogo.domain.pagination.Pagination;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.graphql.server.WebGraphQlHandler;
import org.springframework.graphql.test.tester.WebGraphQlTester;

@IntegrationTest
public class GenreGraphQLIT {

  @MockBean
  private ListGenreUseCase listGenreUseCase;

  @MockBean
  private SaveGenreUseCase saveGenreUseCase;

  @Autowired
  private WebGraphQlHandler webGraphQlHandler;

  @Autowired
  private WebGraphQlSecurityInterceptor interceptor;

  @Test
  public void givenAnonymousUser_whenQueries_shouldReturnUnauthorized() {
    interceptor.setAuthorities();
    final var document = "query genres { genres { id } }";
    final var graphQlTesters = WebGraphQlTester.create(webGraphQlHandler);
    graphQlTesters.document(document).execute()
        .errors().expect(err -> "Unauthorized".equals(err.getMessage()) && "genres".equals(err.getPath()))
        .verify();
  }

  @Test
  public void givenUserWithAdminRole_whenQueries_shouldReturnResult() {
    interceptor.setAuthorities(ROLE_ADMIN);

    final var genres = List.of(
        ListGenreUseCase.Output.from(Fixture.Genres.business()),
        ListGenreUseCase.Output.from(Fixture.Genres.tech())
    );

    final var expectedIds = genres.stream().map(ListGenreUseCase.Output::id).toList();

    when(this.listGenreUseCase.execute(any()))
        .thenReturn(new Pagination<>(0, 10, genres.size(), genres));

    final var document = "query genres { genres { id } }";
    final var graphQlTesters = WebGraphQlTester.create(webGraphQlHandler);
    graphQlTesters.document(document).execute()
        .errors().verify()
        .path("genres[*].id").entityList(String.class).isEqualTo(expectedIds);
  }

  @Test
  public void givenUserWithSubscriberRole_whenQueries_shouldReturnResult() {
    interceptor.setAuthorities(ROLE_SUBSCRIBER);

    final var genres = List.of(
        ListGenreUseCase.Output.from(Fixture.Genres.business()),
        ListGenreUseCase.Output.from(Fixture.Genres.tech())
    );

    final var expectedIds = genres.stream().map(ListGenreUseCase.Output::id).toList();

    when(this.listGenreUseCase.execute(any()))
        .thenReturn(new Pagination<>(0, 10, genres.size(), genres));

    final var document = "query genres { genres { id } }";
    final var graphQlTesters = WebGraphQlTester.create(webGraphQlHandler);
    graphQlTesters.document(document).execute()
        .errors().verify()
        .path("genres[*].id").entityList(String.class).isEqualTo(expectedIds);
  }

  @Test
  public void givenUserWithGenresRole_whenQueries_shouldReturnResult() {
    interceptor.setAuthorities(ROLE_GENRES);

    final var genres = List.of(
        ListGenreUseCase.Output.from(Fixture.Genres.business()),
        ListGenreUseCase.Output.from(Fixture.Genres.tech())
    );

    final var expectedIds = genres.stream().map(ListGenreUseCase.Output::id).toList();

    when(this.listGenreUseCase.execute(any()))
        .thenReturn(new Pagination<>(0, 10, genres.size(), genres));

    final var document = "query genres { genres { id } }";
    final var graphQlTesters = WebGraphQlTester.create(webGraphQlHandler);
    graphQlTesters.document(document).execute()
        .errors().verify()
        .path("genres[*].id").entityList(String.class).isEqualTo(expectedIds);
  }
}
