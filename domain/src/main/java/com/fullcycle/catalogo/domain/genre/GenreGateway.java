package com.fullcycle.catalogo.domain.genre;

import com.fullcycle.catalogo.domain.pagination.Pagination;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.StreamSupport;

public interface GenreGateway {

  Genre save(Genre aGenre);

  void deleteById(String genreId);

  Optional<Genre> findById(String genreId);

  Pagination<Genre> findAll(GenreSearchQuery aQuery);

  List<Genre> findAllById(Set<String> ids);
}
