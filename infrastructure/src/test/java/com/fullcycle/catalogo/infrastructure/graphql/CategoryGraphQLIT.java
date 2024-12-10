package com.fullcycle.catalogo.infrastructure.graphql;

import static com.fullcycle.catalogo.infrastructure.configuration.security.Roles.ROLE_ADMIN;
import static com.fullcycle.catalogo.infrastructure.configuration.security.Roles.ROLE_CATEGORIES;
import static com.fullcycle.catalogo.infrastructure.configuration.security.Roles.ROLE_SUBSCRIBER;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.fullcycle.catalogo.IntegrationTest;
import com.fullcycle.catalogo.WebGraphQlSecurityInterceptor;
import com.fullcycle.catalogo.application.category.list.ListCategoryOutput;
import com.fullcycle.catalogo.application.category.list.ListCategoryUseCase;
import com.fullcycle.catalogo.application.category.save.SaveCategoryUseCase;
import com.fullcycle.catalogo.domain.Fixture;
import com.fullcycle.catalogo.domain.pagination.Pagination;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.graphql.server.WebGraphQlHandler;
import org.springframework.graphql.test.tester.WebGraphQlTester;

@IntegrationTest
public class CategoryGraphQLIT {

  @MockBean
  private ListCategoryUseCase listCategoryUseCase;

  @MockBean
  private SaveCategoryUseCase saveCategoryUseCase;

  @Autowired
  private WebGraphQlHandler webGraphQlHandler;

  @Autowired
  private WebGraphQlSecurityInterceptor interceptor;

  @Test
  public void givenAnonymousUser_whenQueries_shouldReturnUnauthorized() {
    interceptor.setAuthorities();
    final var document = "query categories { categories { id } }";
    final var graphQlTesters = WebGraphQlTester.create(webGraphQlHandler);
    graphQlTesters.document(document).execute()
        .errors().expect(err -> "Unauthorized".equals(err.getMessage()) && "categories".equals(err.getPath()))
        .verify();
  }

  @Test
  public void givenUserWithAdminRole_whenQueries_shouldReturnResult() {
    interceptor.setAuthorities(ROLE_ADMIN);

    final var categories = List.of(
        ListCategoryOutput.from(Fixture.Categories.lives()),
        ListCategoryOutput.from(Fixture.Categories.aulas())
    );

    final var expectedIds = categories.stream().map(ListCategoryOutput::id).toList();

    when(this.listCategoryUseCase.execute(any()))
        .thenReturn(new Pagination<>(0, 10, categories.size(), categories));

    final var document = "query categories { categories { id } }";
    final var graphQlTesters = WebGraphQlTester.create(webGraphQlHandler);
    graphQlTesters.document(document).execute()
        .errors().verify()
        .path("categories[*].id").entityList(String.class).isEqualTo(expectedIds);
  }

  @Test
  public void givenUserWithSubscriberRole_whenQueries_shouldReturnResult() {
    interceptor.setAuthorities(ROLE_SUBSCRIBER);

    final var categories = List.of(
        ListCategoryOutput.from(Fixture.Categories.lives()),
        ListCategoryOutput.from(Fixture.Categories.aulas())
    );

    final var expectedIds = categories.stream().map(ListCategoryOutput::id).toList();

    when(this.listCategoryUseCase.execute(any()))
        .thenReturn(new Pagination<>(0, 10, categories.size(), categories));

    final var document = "query categories { categories { id } }";
    final var graphQlTesters = WebGraphQlTester.create(webGraphQlHandler);
    graphQlTesters.document(document).execute()
        .errors().verify()
        .path("categories[*].id").entityList(String.class).isEqualTo(expectedIds);
  }

  @Test
  public void givenUserWithCategoriesRole_whenQueries_shouldReturnResult() {
    interceptor.setAuthorities(ROLE_CATEGORIES);

    final var categories = List.of(
        ListCategoryOutput.from(Fixture.Categories.lives()),
        ListCategoryOutput.from(Fixture.Categories.aulas())
    );

    final var expectedIds = categories.stream().map(ListCategoryOutput::id).toList();

    when(this.listCategoryUseCase.execute(any()))
        .thenReturn(new Pagination<>(0, 10, categories.size(), categories));

    final var document = "query categories { categories { id } }";
    final var graphQlTesters = WebGraphQlTester.create(webGraphQlHandler);
    graphQlTesters.document(document).execute()
        .errors().verify()
        .path("categories[*].id").entityList(String.class).isEqualTo(expectedIds);
  }
}
