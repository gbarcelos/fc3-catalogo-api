package com.fullcycle.catalogo.infrastructure.video;

import com.fullcycle.catalogo.AbstractElasticsearchTest;
import com.fullcycle.catalogo.domain.Fixture;
import com.fullcycle.catalogo.domain.utils.IdUtils;
import com.fullcycle.catalogo.domain.utils.InstantUtils;
import com.fullcycle.catalogo.domain.video.Rating;
import com.fullcycle.catalogo.domain.video.Video;
import com.fullcycle.catalogo.infrastructure.video.persistence.VideoRepository;
import java.time.Year;
import java.util.Set;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class VideoElasticsearchGatewayTest extends AbstractElasticsearchTest {

  @Autowired
  private VideoElasticsearchGateway videoGateway;

  @Autowired
  private VideoRepository videoRepository;

  @Test
  public void testInjection() {
    Assertions.assertNotNull(this.videoRepository);
    Assertions.assertNotNull(this.videoGateway);
  }

  @Test
  public void givenValidInput_whenCallsSave_shouldPersistIt() {
    // given
    final var expectedId = IdUtils.uniqueId();
    final var expectedTitle = "System Design Interviews";
    final var expectedDescription = """
        Disclaimer: o estudo de caso apresentado tem fins educacionais e representa nossas opiniões pessoais.
        Esse vídeo faz parte da Imersão Full Stack && Full Cycle.
        Para acessar todas as aulas, lives e desafios, acesse:
        https://imersao.fullcycle.com.br/
        """;
    final var expectedLaunchedAt = Year.of(2022);
    final var expectedDuration = 120.10;
    final var expectedOpened = true;
    final var expectedPublished = true;
    final var expectedRating = Rating.L;
    final var expectedCreatedAt = InstantUtils.now();
    final var expectedUpdatedAt = InstantUtils.now();
    final var expectedCategories = Set.of(IdUtils.uniqueId());
    final var expectedCastMembers = Set.of(IdUtils.uniqueId());
    final var expectedGenres = Set.of(IdUtils.uniqueId());
    final var expectedVideo = "http://video";
    final var expectedTrailer = "http://trailer";
    final var expectedBanner = "http://banner";
    final var expectedThumbnail = "http://thumb";
    final var expectedThumbnailHalf = "http://thumbhalf";

    Assertions.assertEquals(0, this.videoRepository.count());

    // when
    final var input = Video.with(
        expectedId,
        expectedTitle,
        expectedDescription,
        expectedLaunchedAt.getValue(),
        expectedDuration,
        expectedRating.getName(),
        expectedOpened,
        expectedPublished,
        expectedCreatedAt.toString(),
        expectedUpdatedAt.toString(),
        expectedVideo,
        expectedTrailer,
        expectedBanner,
        expectedThumbnail,
        expectedThumbnailHalf,
        expectedCategories,
        expectedCastMembers,
        expectedGenres
    );

    final var actualOutput = this.videoGateway.save(input);

    // then
    Assertions.assertEquals(1, this.videoRepository.count());

    Assertions.assertEquals(input, actualOutput);

    var actualVideo = this.videoRepository.findById(expectedId).get();
    Assertions.assertNotNull(actualVideo);
    Assertions.assertEquals(expectedId, actualVideo.id());
    Assertions.assertEquals(expectedCreatedAt.toString(), actualVideo.createdAt());
    Assertions.assertEquals(expectedUpdatedAt.toString(), actualVideo.updatedAt());
    Assertions.assertEquals(expectedTitle, actualVideo.title());
    Assertions.assertEquals(expectedDescription, actualVideo.description());
    Assertions.assertEquals(expectedLaunchedAt.getValue(), actualVideo.launchedAt());
    Assertions.assertEquals(expectedDuration, actualVideo.duration());
    Assertions.assertEquals(expectedOpened, actualVideo.opened());
    Assertions.assertEquals(expectedPublished, actualVideo.published());
    Assertions.assertEquals(expectedRating.getName(), actualVideo.rating());
    Assertions.assertEquals(expectedCategories, actualVideo.categories());
    Assertions.assertEquals(expectedGenres, actualVideo.genres());
    Assertions.assertEquals(expectedCastMembers, actualVideo.castMembers());
    Assertions.assertEquals(expectedVideo, actualVideo.video());
    Assertions.assertEquals(expectedTrailer, actualVideo.trailer());
    Assertions.assertEquals(expectedBanner, actualVideo.banner());
    Assertions.assertEquals(expectedThumbnail, actualVideo.thumbnail());
    Assertions.assertEquals(expectedThumbnailHalf, actualVideo.thumbnailHalf());
  }

  @Test
  public void givenMinimalInput_whenCallsSave_shouldPersistIt() {
    // given
    final var expectedId = IdUtils.uniqueId();
    final var expectedTitle = "Java 21";
    final var expectedRating = Fixture.Videos.rating();
    final var expectedDuration = 2.0;
    final var expectedLaunchedAt = Year.of(2024);
    final var expectedDates = InstantUtils.now();
    final String expectedDescription = null;
    final Boolean expectedOpened = false;
    final Boolean expectedPublished = false;
    final String expectedVideo = null;
    final String expectedTrailer = null;
    final String expectedBanner = null;
    final String expectedThumbnail = null;
    final String expectedThumbnailHalf = null;
    final Set<String> expectedCastMembers = Set.of();
    final Set<String> expectedCategories = Set.of();
    final Set<String> expectedGenres = Set.of();

    Assertions.assertEquals(0, this.videoRepository.count());

    // when
    final var input = Video.with(
        expectedId,
        expectedTitle,
        expectedDescription,
        expectedLaunchedAt.getValue(),
        expectedDuration,
        expectedRating.getName(),
        expectedOpened,
        expectedPublished,
        expectedDates.toString(),
        expectedDates.toString(),
        expectedVideo,
        expectedTrailer,
        expectedBanner,
        expectedThumbnail,
        expectedThumbnailHalf,
        expectedCategories,
        expectedCastMembers,
        expectedGenres
    );

    final var actualOutput = this.videoGateway.save(input);

    // then
    Assertions.assertEquals(1, this.videoRepository.count());

    Assertions.assertEquals(input, actualOutput);

    var actualVideo = this.videoRepository.findById(expectedId).get();
    Assertions.assertNotNull(actualVideo);
    Assertions.assertEquals(expectedId, actualVideo.id());
    Assertions.assertEquals(expectedDates.toString(), actualVideo.createdAt());
    Assertions.assertEquals(expectedDates.toString(), actualVideo.updatedAt());
    Assertions.assertEquals(expectedTitle, actualVideo.title());
    Assertions.assertEquals(expectedDescription, actualVideo.description());
    Assertions.assertEquals(expectedLaunchedAt.getValue(), actualVideo.launchedAt());
    Assertions.assertEquals(expectedDuration, actualVideo.duration());
    Assertions.assertEquals(expectedOpened, actualVideo.opened());
    Assertions.assertEquals(expectedPublished, actualVideo.published());
    Assertions.assertEquals(expectedRating.getName(), actualVideo.rating());
    Assertions.assertEquals(expectedCategories, actualVideo.categories());
    Assertions.assertEquals(expectedGenres, actualVideo.genres());
    Assertions.assertEquals(expectedCastMembers, actualVideo.castMembers());
    Assertions.assertEquals(expectedVideo, actualVideo.video());
    Assertions.assertEquals(expectedTrailer, actualVideo.trailer());
    Assertions.assertEquals(expectedBanner, actualVideo.banner());
    Assertions.assertEquals(expectedThumbnail, actualVideo.thumbnail());
    Assertions.assertEquals(expectedThumbnailHalf, actualVideo.thumbnailHalf());
  }
}