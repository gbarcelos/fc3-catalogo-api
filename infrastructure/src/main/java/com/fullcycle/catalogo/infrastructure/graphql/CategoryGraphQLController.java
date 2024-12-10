package com.fullcycle.catalogo.infrastructure.graphql;

import static com.fullcycle.catalogo.infrastructure.configuration.security.Roles.ROLE_ADMIN;
import static com.fullcycle.catalogo.infrastructure.configuration.security.Roles.ROLE_CATEGORIES;
import static com.fullcycle.catalogo.infrastructure.configuration.security.Roles.ROLE_SUBSCRIBER;

import com.fullcycle.catalogo.application.category.list.ListCategoryUseCase;
import com.fullcycle.catalogo.application.category.save.SaveCategoryUseCase;
import com.fullcycle.catalogo.domain.category.CategorySearchQuery;
import com.fullcycle.catalogo.infrastructure.category.GqlCategoryPresenter;
import com.fullcycle.catalogo.infrastructure.category.models.GqlCategory;
import com.fullcycle.catalogo.infrastructure.category.models.GqlCategoryInput;
import java.util.List;
import java.util.Objects;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;

@Controller
public class CategoryGraphQLController {

  private final ListCategoryUseCase listCategoryUseCase;
  private final SaveCategoryUseCase saveCategoryUseCase;

  public CategoryGraphQLController(
      final ListCategoryUseCase listCategoryUseCase,
      final SaveCategoryUseCase saveCategoryUseCase) {
    this.listCategoryUseCase = Objects.requireNonNull(listCategoryUseCase);
    this.saveCategoryUseCase = Objects.requireNonNull(saveCategoryUseCase);
  }

  @QueryMapping
  @Secured({ROLE_ADMIN, ROLE_SUBSCRIBER, ROLE_CATEGORIES})
  public List<GqlCategory> categories(
      @Argument final String search,
      @Argument final int page,
      @Argument final int perPage,
      @Argument final String sort,
      @Argument final String direction
  ) {

    final var aQuery =
        new CategorySearchQuery(page, perPage, search, sort, direction);

    return this.listCategoryUseCase.execute(aQuery)
        .map(GqlCategoryPresenter::present)
        .data();
  }

  @MutationMapping
  @Secured({ROLE_ADMIN, ROLE_SUBSCRIBER, ROLE_CATEGORIES})
  public GqlCategory saveCategory(@Argument final GqlCategoryInput input) {
    return GqlCategoryPresenter.present(this.saveCategoryUseCase.execute(input.toCategory()));
  }
}
