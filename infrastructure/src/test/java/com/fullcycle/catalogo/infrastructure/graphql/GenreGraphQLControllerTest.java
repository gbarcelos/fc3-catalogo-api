package com.fullcycle.catalogo.infrastructure.graphql;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fullcycle.catalogo.GraphQLControllerTest;
import com.fullcycle.catalogo.application.genre.list.ListGenreUseCase;
import com.fullcycle.catalogo.application.genre.save.SaveGenreUseCase;
import com.fullcycle.catalogo.domain.Fixture;
import com.fullcycle.catalogo.domain.pagination.Pagination;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.graphql.test.tester.GraphQlTester;

@GraphQLControllerTest(controllers = GenreGraphQLController.class)
public class GenreGraphQLControllerTest {

  private static final ParameterizedTypeReference<Set<String>> CATEGORIES_TYPE = new ParameterizedTypeReference<>() {
  };

  @MockBean
  private ListGenreUseCase listGenreUseCase;

  @MockBean
  private SaveGenreUseCase saveGenreUseCase;

  @Autowired
  private GraphQlTester graphql;

  @Test
  public void givenDefaultArgumentsWhenCallsListGenresShouldReturn() {
    // given
    final var expectedGenres = List.of(
        ListGenreUseCase.Output.from(Fixture.Genres.business()),
        ListGenreUseCase.Output.from(Fixture.Genres.tech())
    );

    final var expectedPage = 0;
    final var expectedPerPage = 10;
    final var expectedSort = "name";
    final var expectedDirection = "asc";
    final var expectedSearch = "";
    final var expectedCategories = Set.of();

    when(this.listGenreUseCase.execute(any()))
        .thenReturn(new Pagination<>(expectedPage, expectedPerPage, expectedGenres.size(), expectedGenres));

    final var query = """
                {
                  genres {
                    id
                    name
                    active
                    categories
                    createdAt
                    updatedAt
                    deletedAt
                  }
                }
                """;

    // when
    final var res = this.graphql.document(query).execute();

    final var actualGenres = res.path("genres")
        .entityList(ListGenreUseCase.Output.class)
        .get();

    // then
    Assertions.assertTrue(
        actualGenres.size() == expectedGenres.size()
            && actualGenres.containsAll(expectedGenres)
    );

    final var capturer = ArgumentCaptor.forClass(ListGenreUseCase.Input.class);

    verify(this.listGenreUseCase, times(1)).execute(capturer.capture());

    final var actualQuery = capturer.getValue();
    Assertions.assertEquals(expectedPage, actualQuery.page());
    Assertions.assertEquals(expectedPerPage, actualQuery.perPage());
    Assertions.assertEquals(expectedSort, actualQuery.sort());
    Assertions.assertEquals(expectedDirection, actualQuery.direction());
    Assertions.assertEquals(expectedSearch, actualQuery.terms());
    Assertions.assertEquals(expectedCategories, actualQuery.categories());
  }

  @Test
  public void givenCustomArgumentsWhenCallsListGenresShouldReturn() {
    // given
    final var expectedGenres = List.of(
        ListGenreUseCase.Output.from(Fixture.Genres.business()),
        ListGenreUseCase.Output.from(Fixture.Genres.tech())
    );

    final var expectedPage = 2;
    final var expectedPerPage = 15;
    final var expectedSort = "id";
    final var expectedDirection = "desc";
    final var expectedSearch = "asd";
    final var expectedCategories = Set.of("c1");

    when(this.listGenreUseCase.execute(any()))
        .thenReturn(new Pagination<>(expectedPage, expectedPerPage, expectedGenres.size(), expectedGenres));

    final var query = """
                query AllGenres($search: String, $page: Int, $perPage: Int, $sort: String, $direction: String, $categories: [String]) {
                                
                  genres(search: $search, page: $page, perPage: $perPage, sort: $sort, direction: $direction, categories: $categories) {
                    id
                    name
                    active
                    categories
                    createdAt
                    updatedAt
                    deletedAt
                  }
                }
                """;

    // when
    final var res = this.graphql.document(query)
        .variable("search", expectedSearch)
        .variable("page", expectedPage)
        .variable("perPage", expectedPerPage)
        .variable("sort", expectedSort)
        .variable("direction", expectedDirection)
        .variable("categories", expectedCategories)
        .execute();

    final var actualGenres = res.path("genres")
        .entityList(ListGenreUseCase.Output.class)
        .get();

    // then
    Assertions.assertTrue(
        actualGenres.size() == expectedGenres.size()
            && actualGenres.containsAll(expectedGenres)
    );

    final var capturer = ArgumentCaptor.forClass(ListGenreUseCase.Input.class);

    verify(this.listGenreUseCase, times(1)).execute(capturer.capture());

    final var actualQuery = capturer.getValue();
    Assertions.assertEquals(expectedPage, actualQuery.page());
    Assertions.assertEquals(expectedPerPage, actualQuery.perPage());
    Assertions.assertEquals(expectedSort, actualQuery.sort());
    Assertions.assertEquals(expectedDirection, actualQuery.direction());
    Assertions.assertEquals(expectedSearch, actualQuery.terms());
    Assertions.assertEquals(expectedCategories, actualQuery.categories());
  }
}
