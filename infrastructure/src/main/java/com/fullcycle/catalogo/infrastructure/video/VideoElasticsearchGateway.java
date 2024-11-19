package com.fullcycle.catalogo.infrastructure.video;

import com.fullcycle.catalogo.domain.pagination.Pagination;
import com.fullcycle.catalogo.domain.video.Video;
import com.fullcycle.catalogo.domain.video.VideoGateway;
import com.fullcycle.catalogo.domain.video.VideoSearchQuery;
import com.fullcycle.catalogo.infrastructure.video.persistence.VideoDocument;
import com.fullcycle.catalogo.infrastructure.video.persistence.VideoRepository;
import java.util.Objects;
import java.util.Optional;
import org.springframework.context.annotation.Profile;
import org.springframework.data.elasticsearch.core.SearchOperations;
import org.springframework.stereotype.Component;

@Component
@Profile("!development")
public class VideoElasticsearchGateway implements VideoGateway {

  private final VideoRepository videoRepository;
  private final SearchOperations searchOperations;

  public VideoElasticsearchGateway(final VideoRepository videoRepository,
      final SearchOperations searchOperations) {
    this.videoRepository = Objects.requireNonNull(videoRepository);
    this.searchOperations = Objects.requireNonNull(searchOperations);
  }

  @Override
  public Video save(final Video video) {
    this.videoRepository.save(VideoDocument.from(video));
    return video;
  }

  @Override
  public void deleteById(final String videoId) {
    if (videoId == null || videoId.isBlank()) {
      return;
    }
    this.videoRepository.deleteById(videoId);
  }

  @Override
  public Optional<Video> findById(final String videoId) {
    if (videoId == null || videoId.isBlank()) {
      return Optional.empty();
    }
    return this.videoRepository.findById(videoId)
        .map(VideoDocument::toVideo);
  }

  @Override
  public Pagination<Video> findAll(final VideoSearchQuery aQuery) {
    return null;
  }
}
