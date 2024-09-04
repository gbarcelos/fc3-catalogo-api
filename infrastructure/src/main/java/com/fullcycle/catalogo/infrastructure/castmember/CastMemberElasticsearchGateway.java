package com.fullcycle.catalogo.infrastructure.castmember;

import com.fullcycle.catalogo.domain.castmember.CastMember;
import com.fullcycle.catalogo.domain.castmember.CastMemberGateway;
import com.fullcycle.catalogo.domain.castmember.CastMemberSearchQuery;
import com.fullcycle.catalogo.domain.pagination.Pagination;
import com.fullcycle.catalogo.infrastructure.castmember.persistence.CastMemberDocument;
import com.fullcycle.catalogo.infrastructure.castmember.persistence.CastMemberRepository;
import java.util.Objects;
import java.util.Optional;
import org.springframework.data.elasticsearch.core.SearchOperations;
import org.springframework.stereotype.Component;

@Component
public class CastMemberElasticsearchGateway implements CastMemberGateway {

  private static final String NAME_PROP = "name";
  private static final String KEYWORD = ".keyword";

  private final CastMemberRepository castMemberRepository;
  private final SearchOperations searchOperations;

  public CastMemberElasticsearchGateway(
      final CastMemberRepository castMemberRepository,
      final SearchOperations searchOperations
  ) {
    this.castMemberRepository = Objects.requireNonNull(castMemberRepository);
    this.searchOperations = Objects.requireNonNull(searchOperations);
  }

  @Override
  public CastMember save(final CastMember aMember) {
    this.castMemberRepository.save(CastMemberDocument.from(aMember));
    return aMember;
  }

  @Override
  public void deleteById(final String anId) {
    this.castMemberRepository.deleteById(anId);
  }

  @Override
  public Optional<CastMember> findById(final String anId) {
    return this.castMemberRepository.findById(anId)
        .map(CastMemberDocument::toCastMember);
  }

  @Override
  public Pagination<CastMember> findAll(final CastMemberSearchQuery aQuery) {
    return null;
  }
}
