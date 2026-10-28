package com.chas.blog.service;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import com.chas.blog.dto.CreatePostRequest;
import com.chas.blog.dto.PostSummary;
import com.chas.blog.dto.PostView;
import com.chas.blog.exceptions.CannotPublishWithoutTranslationException;
import com.chas.blog.exceptions.CannotSchedulePostInThePastException;
import com.chas.blog.exceptions.CannotSchedulePublishedPostException;
import com.chas.blog.exceptions.PostNotFoundException;
import com.chas.blog.model.post.Language;
import com.chas.blog.model.post.Post;
import com.chas.blog.model.post.PostStatus;
import com.chas.blog.model.post.PostTranslation;
import com.chas.blog.repository.PostRepository;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
public class PostServiceTest {

	@Mock
	PostRepository repository;
	Clock clock = Clock.fixed(Instant.parse("2026-10-22T12:00:00Z"), ZoneOffset.UTC);

	PostService service;

	@BeforeEach
	void setUp() {
		service = new PostServiceImpl(repository, clock);
	}

	@Test
	void testCreatePostDraft() {

		// given
		var request = new CreatePostRequest(
				"My Title is this",
				"Summary",
				"Content",
				1L,
				Language.EN);

		when(repository.save(any(Post.class)))
				.thenAnswer(invocation -> invocation.getArgument(0));

		// when
		service.createDraft(request);

		// then

		ArgumentCaptor<Post> postCaptor = ArgumentCaptor.forClass(Post.class);

		verify(repository).save(postCaptor.capture());

		Post postSaved = postCaptor.getValue();

		assertThat(postSaved.getStatus()).isEqualTo(PostStatus.DRAFT);
		assertThat(postSaved.getAuthorId()).isEqualTo(1L);
		assertThat(postSaved.getPublishedAt()).isNull();
		assertThat(postSaved.getTranslations()).hasSize(1);

		PostTranslation postTranslation = postSaved.getTranslations().get(0);

		assertThat(postTranslation.getLanguage()).isEqualTo(Language.EN);
		assertThat(postTranslation.getTitle()).isEqualTo("My Title is this");
		assertThat(postTranslation.getSummary()).isEqualTo("Summary");
		assertThat(postTranslation.getContent()).isEqualTo("Content");
		assertThat(postTranslation.getSlug()).isEqualTo("my-title-is-this");
	}

	@Test
	void createDraftReturnsIdOfSavedPost() {

		// given
		when(repository.save(any(Post.class))).thenAnswer(invocation -> {
			Post post = invocation.getArgument(0);
			ReflectionTestUtils.setField(post, "id", 42L);
			return post;
		});

		// when
		Long id = service.createDraft(new CreatePostRequest(
				"title",
				"summary",
				"content",
				1L,
				Language.EN));

		// then
		assertThat(id).isEqualTo(42L);

	}

	// publish post tests

	@Test
	void publishPost() {
		// given

		Post post = Post.createDraft(1L);
		post.addTranslation(Language.EN, "title", "slug", "summary", "content");
		post.addTranslation(Language.PT, "title", "slug", "summary", "content");
		when(repository.findById(1L)).thenReturn(Optional.of(post));

		// when
		service.publish(1L);

		// then
		assertThat(post.getStatus()).isEqualTo(PostStatus.PUBLISHED);
		assertThat(post.getPublishedAt()).isEqualTo(clock.instant());

	}

	@Test
	void publishPostThrowsExceptionWhenPostNotFound() {
		// given
		when(repository.findById(1L)).thenReturn(Optional.empty());

		// when
		assertThatThrownBy(() -> service.publish(1L))
				.isInstanceOf(PostNotFoundException.class)
				.hasMessage("Post not found: 1");

	}

	// schedule post tests

	@Test
	void schedulePost() {

		// given
		Post post = Post.createDraft(1L);
		post.addTranslation(Language.EN, "title", "slug", "summary", "content");
		post.addTranslation(Language.PT, "title", "slug", "summary", "content");
		when(repository.findById(1L)).thenReturn(Optional.of(post));

		// when
		service.schedule(1L, Instant.parse("2026-10-23T12:00:00Z"));

		// then
		assertThat(post.getStatus()).isEqualTo(PostStatus.PUBLISHED);
		assertThat(post.getPublishedAt()).isEqualTo(Instant.parse("2026-10-23T12:00:00Z"));
	}

	@Test
	void schedulePostThrowsExceptionWhenPostNotFound() {
		// given
		when(repository.findById(1L)).thenReturn(Optional.empty());
		Instant now = clock.instant().plus(1, ChronoUnit.DAYS);

		// when
		assertThatThrownBy(() -> service.schedule(1L, now))
				.isInstanceOf(PostNotFoundException.class)
				.hasMessage("Post not found: 1");

	}

	@Test 
	void schedulePostThrowsExceptionWhenPostTranslationsAreMissing() {
		// given
		Post post = Post.createDraft(1L);
		when(repository.findById(1L)).thenReturn(Optional.of(post));
		Instant now = clock.instant().plus(1, ChronoUnit.DAYS);

		// when
		assertThatThrownBy(() -> service.schedule(1L, now))
				.isInstanceOf(CannotPublishWithoutTranslationException.class)
				.hasMessage("Cannot publish a post without translations in EN and PT");
	} 

	@Test 
	void schedulePostThrowsExceptionWhenScheduledInThePast() {
		// given
		Instant past = clock.instant().minus(1, ChronoUnit.DAYS);

		// when
		assertThatThrownBy(() -> service.schedule(1L, past))
				.isInstanceOf(CannotSchedulePostInThePastException.class)
				.hasMessage("Cannot schedule a post in the past");
	}

	@Test
	void schedulePostThrowsExceptionWhenPostAlreadyPublished() {
		// given
		Post post = Post.createDraft(42L);
		post.addTranslation(Language.EN, "title", "slug", "summary", "content");
		post.addTranslation(Language.PT, "title", "slug", "summary", "content");
		post.publish(clock.instant());

		when(repository.findById(42L)).thenReturn(Optional.of(post));

		// when then
		assertThatThrownBy(() -> service.schedule(42L, clock.instant().plus(1, ChronoUnit.DAYS)))
			.isInstanceOf(CannotSchedulePublishedPostException.class)
			.hasMessage("Cannot schedule a post that is already published");
	}

	private PostTranslation translation(Language language, String title, String slug) {
		Post post = Post.createDraft(1L);
		post.addTranslation(language, title, slug, "Summary of " + title, "Content of " + title);
		return post.getTranslations().get(0);
	}

	@Test
	void findPublishedReturnsViewOfTranslation() {
		// given
		PostTranslation translation = translation(Language.PT, "Olá Mundo", "ola-mundo");
		when(repository.findPublishedSlug(Language.PT, "ola-mundo", clock.instant()))
				.thenReturn(Optional.of(translation));

		// when
		PostView view = service.findPublished(Language.PT, "ola-mundo");

		// then
		assertThat(view.title()).isEqualTo("Olá Mundo");
		assertThat(view.slug()).isEqualTo("ola-mundo");
		assertThat(view.summary()).isEqualTo("Summary of Olá Mundo");
		assertThat(view.content()).isEqualTo("Content of Olá Mundo");
		assertThat(view.language()).isEqualTo(Language.PT);
	}

	@Test
	void findPublishedThrowsWhenNotFound() {
		// given
		when(repository.findPublishedSlug(Language.EN, "missing", clock.instant()))
				.thenReturn(Optional.empty());

		// when / then
		assertThatThrownBy(() -> service.findPublished(Language.EN, "missing"))
				.isInstanceOf(PostNotFoundException.class)
				.hasMessage("Post not found: missing");
	}

	@Test
	void listPublishedMapsTranslationsToSummaries() {
		// given
		Pageable pageable = PageRequest.of(1, 2);   // segunda página, 2 itens por página
		List<PostTranslation> content = List.of(
				translation(Language.EN, "First", "first"),
				translation(Language.EN, "Second", "second"));
		when(repository.findByLanguage(Language.EN, clock.instant(), pageable))
				.thenReturn(new PageImpl<>(content, pageable, 5));

		// when
		Page<PostSummary> page = service.listPublished(Language.EN, pageable);

		// then: conteúdo convertido, na mesma ordem
		assertThat(page.getContent())
				.extracting(PostSummary::title)
				.containsExactly("First", "Second");
		assertThat(page.getContent().get(0).summary()).isEqualTo("Summary of First");

		// then: metadados preservados
		assertThat(page.getNumber()).isEqualTo(1);
		assertThat(page.getSize()).isEqualTo(2);
		assertThat(page.getTotalElements()).isEqualTo(5);
		assertThat(page.getTotalPages()).isEqualTo(3);
	}

	@Test
	void listPublishedReturnsEmptyPageWhenNothingPublished() {
		// given
		Pageable pageable = PageRequest.of(0, 10);
		when(repository.findByLanguage(Language.PT, clock.instant(), pageable))
				.thenReturn(Page.empty(pageable));

		// when
		Page<PostSummary> page = service.listPublished(Language.PT, pageable);

		// then
		assertThat(page.getContent()).isEmpty();
		assertThat(page.getTotalElements()).isZero();
	}

}
