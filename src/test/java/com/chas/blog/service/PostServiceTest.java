package com.chas.blog.service;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import org.apache.commons.codec.language.bm.Lang;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.chas.blog.dto.CreatePostRequest;
import com.chas.blog.model.post.Language;
import com.chas.blog.model.post.Post;
import com.chas.blog.model.post.PostStatus;
import com.chas.blog.model.post.PostTranslation;
import com.chas.blog.repository.PostRepository;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
import static org.assertj.core.api.Assertions.assertThat;

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

		//given
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
			Language.EN
		));

		// then
		assertThat(id).isEqualTo(42L);

	}

}
