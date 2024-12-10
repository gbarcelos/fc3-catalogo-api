package com.fullcycle.catalogo.infrastructure.graphql;

import static com.fullcycle.catalogo.infrastructure.configuration.security.Roles.ROLE_ADMIN;
import static com.fullcycle.catalogo.infrastructure.configuration.security.Roles.ROLE_GENRES;
import static com.fullcycle.catalogo.infrastructure.configuration.security.Roles.ROLE_SUBSCRIBER;

import com.fullcycle.catalogo.application.genre.list.ListGenreUseCase;
import com.fullcycle.catalogo.application.genre.save.SaveGenreUseCase;
import com.fullcycle.catalogo.infrastructure.genre.GqlGenrePresenter;
import com.fullcycle.catalogo.infrastructure.genre.models.GqlGenre;
import com.fullcycle.catalogo.infrastructure.genre.models.GqlGenreInput;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;

@Controller
public class GenreGraphQLController {

  private final ListGenreUseCase listGenreUseCase;
  private final SaveGenreUseCase saveGenreUseCase;

  public GenreGraphQLController(final ListGenreUseCase listGenreUseCase,
      final SaveGenreUseCase saveGenreUseCase) {
    this.listGenreUseCase = Objects.requireNonNull(listGenreUseCase);
    this.saveGenreUseCase = Objects.requireNonNull(saveGenreUseCase);
  }

  @QueryMapping
  @Secured({ROLE_ADMIN, ROLE_SUBSCRIBER, ROLE_GENRES})
  public List<GqlGenre> genres(
      @Argument final String search,
      @Argument final int page,
      @Argument final int perPage,
      @Argument final String sort,
      @Argument final String direction,
      @Argument final Set<String> categories
  ) {
    final var input = new ListGenreUseCase.Input(page, perPage, search, sort, direction,
        categories);
    return this.listGenreUseCase.execute(input)
        .map(GqlGenrePresenter::present)
        .data();
  }

  @MutationMapping
  @Secured({ROLE_ADMIN, ROLE_SUBSCRIBER, ROLE_GENRES})
  public SaveGenreUseCase.Output saveGenre(@Argument(name = "input") final GqlGenreInput arg) {
    final var input =
        new SaveGenreUseCase.Input(arg.id(),
            arg.name(),
            arg.active(),
            arg.categories(),
            arg.createdAt(),
            arg.updatedAt(),
            arg.deletedAt());

    return this.saveGenreUseCase.execute(input);
  }
}