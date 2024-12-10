package com.fullcycle.catalogo.infrastructure.graphql;

import static com.fullcycle.catalogo.infrastructure.configuration.security.Roles.ROLE_ADMIN;
import static com.fullcycle.catalogo.infrastructure.configuration.security.Roles.ROLE_CAST_MEMBERS;
import static com.fullcycle.catalogo.infrastructure.configuration.security.Roles.ROLE_SUBSCRIBER;

import com.fullcycle.catalogo.application.castmember.list.ListCastMemberUseCase;
import com.fullcycle.catalogo.application.castmember.list.ListCastMembersOutput;
import com.fullcycle.catalogo.application.castmember.save.SaveCastMemberUseCase;
import com.fullcycle.catalogo.domain.castmember.CastMember;
import com.fullcycle.catalogo.domain.castmember.CastMemberSearchQuery;
import com.fullcycle.catalogo.infrastructure.castmember.models.GqlCastMemberInput;
import java.util.List;
import java.util.Objects;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;

@Controller
public class CastMemberGraphQLController {

  private final ListCastMemberUseCase listCastMemberUseCase;
  private final SaveCastMemberUseCase saveCastMemberUseCase;

  public CastMemberGraphQLController(
      final ListCastMemberUseCase listCastMemberUseCase,
      final SaveCastMemberUseCase saveCastMemberUseCase
  ) {
    this.listCastMemberUseCase = Objects.requireNonNull(listCastMemberUseCase);
    this.saveCastMemberUseCase = Objects.requireNonNull(saveCastMemberUseCase);
  }

  @QueryMapping
  @Secured({ROLE_ADMIN, ROLE_SUBSCRIBER, ROLE_CAST_MEMBERS})
  public List<ListCastMembersOutput> castMembers(
      @Argument final String search,
      @Argument final int page,
      @Argument final int perPage,
      @Argument final String sort,
      @Argument final String direction
  ) {
    final var query =
        new CastMemberSearchQuery(page, perPage, search, sort, direction);

    return this.listCastMemberUseCase.execute(query).data();
  }

  @MutationMapping
  @Secured({ROLE_ADMIN, ROLE_SUBSCRIBER, ROLE_CAST_MEMBERS})
  public CastMember saveCastMember(@Argument GqlCastMemberInput input) {
    return this.saveCastMemberUseCase.execute(input.toCastMember());
  }
}
