package com.fullcycle.catalogo.infrastructure.graphql;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fullcycle.catalogo.GraphQLControllerTest;
import com.fullcycle.catalogo.application.castmember.get.GetAllCastMembersByIdUseCase;
import com.fullcycle.catalogo.application.category.get.GetAllCategoriesByIdUseCase;
import com.fullcycle.catalogo.application.genre.get.GetAllGenresByIdUseCase;
import com.fullcycle.catalogo.application.video.list.ListVideoUseCase;
import com.fullcycle.catalogo.domain.Fixture;
import com.fullcycle.catalogo.domain.pagination.Pagination;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.graphql.test.tester.GraphQlTester;

@GraphQLControllerTest(controllers = VideoGraphQLController.class)
public class VideoGraphQLControllerTest {

  @MockBean
  private ListVideoUseCase listVideoUseCase;

  @MockBean
  private GetAllCastMembersByIdUseCase getAllCastMembersByIdUseCase;

  @MockBean
  private GetAllCategoriesByIdUseCase getAllCategoriesByIdUseCase;

  @MockBean
  private GetAllGenresByIdUseCase getAllGenresByIdUseCase;

  @Autowired
  private GraphQlTester graphql;

  @Test
  public void givenDefaultArgumentsWhenCallsListVideosShouldReturn() {
    // given
    final var categories = List.of(
        new GetAllCategoriesByIdUseCase.Output(Fixture.Categories.lives()));
    final var castMembers = List.of(
        new GetAllCastMembersByIdUseCase.Output(Fixture.CastMembers.gabriel()));
    final var genres = List.of(new GetAllGenresByIdUseCase.Output(Fixture.Genres.tech()));

    final var expectedVideos = List.of(
        ListVideoUseCase.Output.from(Fixture.Videos.java21()),
        ListVideoUseCase.Output.from(Fixture.Videos.systemDesign())
    );

    final var expectedPage = 0;
    final var expectedPerPage = 10;
    final var expectedSort = "name";
    final var expectedDirection = "asc";
    final var expectedSearch = "";
    final String expectedRating = null;
    final Integer expectedYearLaunched = null;
    final var expectedCastMembers = Set.of();
    final var expectedCategories = Set.of();
    final var expectedGenres = Set.of();

    when(this.listVideoUseCase.execute(any()))
        .thenReturn(
            new Pagination<>(expectedPage, expectedPerPage, expectedVideos.size(), expectedVideos));

    when(this.getAllCastMembersByIdUseCase.execute(any())).thenReturn(castMembers);
    when(this.getAllCategoriesByIdUseCase.execute(any())).thenReturn(categories);
    when(this.getAllGenresByIdUseCase.execute(any())).thenReturn(genres);

    final var query = """
        {
          videos {
            id
            title
            description
            yearLaunched
            rating
            duration
            opened
            published
            video
            trailer
            banner
            thumbnail
            thumbnailHalf
            castMembersId
            castMembers {
                id
                name
                type
                createdAt
                updatedAt
            }
            categoriesId
            categories {
                id
                name
                description
            }
            genresId
            genres {
                id
                name
                active
                categories
                createdAt
                updatedAt
                deletedAt
            }
            createdAt
            updatedAt
          }
        }
        """;

    // when
    final var res = this.graphql.document(query).execute();

    final var actualVideos = res.path("videos")
        .entityList(VideoOutput.class)
        .get();

    // then
    Assertions.assertTrue(
        actualVideos.size() == expectedVideos.size()
            && actualVideos.containsAll(expectedVideos)
    );

    final var capturer = ArgumentCaptor.forClass(ListVideoUseCase.Input.class);

    verify(this.listVideoUseCase, times(1)).execute(capturer.capture());

    final var actualQuery = capturer.getValue();
    Assertions.assertEquals(expectedPage, actualQuery.page());
    Assertions.assertEquals(expectedPerPage, actualQuery.perPage());
    Assertions.assertEquals(expectedSort, actualQuery.sort());
    Assertions.assertEquals(expectedDirection, actualQuery.direction());
    Assertions.assertEquals(expectedSearch, actualQuery.terms());
    Assertions.assertEquals(expectedCastMembers, actualQuery.castMembers());
    Assertions.assertEquals(expectedCategories, actualQuery.categories());
    Assertions.assertEquals(expectedGenres, actualQuery.genres());
    Assertions.assertEquals(expectedYearLaunched, actualQuery.launchedAt());
    Assertions.assertEquals(expectedRating, actualQuery.rating());
  }

  @Test
  public void givenCustomArgumentsWhenCallsListGenresShouldReturn() {
    // given
    record VideoIDOutput(String id) {

    }

    final var java21 = Fixture.Videos.java21();
    final var systemDesign = Fixture.Videos.systemDesign();

    final var expectedVideos = List.of(
        ListVideoUseCase.Output.from(java21),
        ListVideoUseCase.Output.from(systemDesign)
    );

    final var expectedVideosId = List.of(
        new VideoIDOutput(java21.id()),
        new VideoIDOutput(systemDesign.id())
    );

    final var expectedPage = 2;
    final var expectedPerPage = 15;
    final var expectedSort = "id";
    final var expectedDirection = "desc";
    final var expectedSearch = "asd";
    final var expectedRating = "L";
    final int expectedYearLaunched = 2012;
    final var expectedCastMembers = Set.of("ct1");
    final var expectedCategories = Set.of("c1");
    final var expectedGenres = Set.of("g1");

    when(this.listVideoUseCase.execute(any()))
        .thenReturn(
            new Pagination<>(expectedPage, expectedPerPage, expectedGenres.size(), expectedVideos));

    final var query = """
        query AllVideos($search: String, $page: Int, $perPage: Int, $sort: String, $direction: String, $rating: String, $yearLaunched: Int, $castMembers: [String], $categories: [String], $genres: [String]) {
                        
          videos(search: $search, page: $page, perPage: $perPage, sort: $sort, direction: $direction, rating: $rating, yearLaunched: $yearLaunched, castMembers: $castMembers, categories: $categories, genres: $genres) {
            id
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
        .variable("rating", expectedRating)
        .variable("yearLaunched", expectedYearLaunched)
        .variable("castMembers", expectedCastMembers)
        .variable("categories", expectedCategories)
        .variable("genres", expectedGenres)
        .execute();

    final var actualVideos = res.path("videos")
        .entityList(VideoIDOutput.class)
        .get();

    // then
    Assertions.assertTrue(
        actualVideos.size() == expectedVideosId.size()
            && actualVideos.containsAll(expectedVideosId)
    );

    final var capturer = ArgumentCaptor.forClass(ListVideoUseCase.Input.class);

    verify(this.listVideoUseCase, times(1)).execute(capturer.capture());

    final var actualQuery = capturer.getValue();
    Assertions.assertEquals(expectedPage, actualQuery.page());
    Assertions.assertEquals(expectedPerPage, actualQuery.perPage());
    Assertions.assertEquals(expectedSort, actualQuery.sort());
    Assertions.assertEquals(expectedDirection, actualQuery.direction());
    Assertions.assertEquals(expectedSearch, actualQuery.terms());
    Assertions.assertEquals(expectedCastMembers, actualQuery.castMembers());
    Assertions.assertEquals(expectedCategories, actualQuery.categories());
    Assertions.assertEquals(expectedGenres, actualQuery.genres());
    Assertions.assertEquals(expectedYearLaunched, actualQuery.launchedAt());
    Assertions.assertEquals(expectedRating, actualQuery.rating());
  }

  public record VideoOutput(
      String id,
      String title,
      String description,
      int yearLaunched,
      String rating,
      Double duration,
      boolean opened,
      boolean published,
      String video,
      String trailer,
      String banner,
      String thumbnail,
      String thumbnailHalf,
      Set<String> categoriesId,
      Set<GetAllCategoriesByIdUseCase.Output> categories,
      Set<String> castMembersId,
      Set<GetAllCastMembersByIdUseCase.Output> castMembers,
      Set<String> genresId,
      Set<GetAllGenresByIdUseCase.Output> genres,
      Instant createdAt,
      Instant updatedAt
  ) {

  }
}
