package com.chas.blog.controller;

import java.time.Instant;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.assertj.MvcTestResult;

import com.chas.blog.dto.CreatePostRequest;
import com.chas.blog.dto.TranslationRequest;
import com.chas.blog.exceptions.post.CannotPublishWithoutTranslationException;
import com.chas.blog.exceptions.post.CannotSchedulePublishedPostException;
import com.chas.blog.exceptions.post.PostNotFoundException;
import com.chas.blog.exceptions.post.SlugAlreadyExistsException;
import com.chas.blog.security.SecurityConfig;
import com.chas.blog.service.PostService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;

@WebMvcTest
@Import(SecurityConfig.class)
public class AdminPostControllerTest {

    private static final Instant PUBLISHED_AT = Instant.parse("2026-10-20T12:00:00Z");

    @Autowired
    private MockMvcTester mockMvc;

    @MockitoBean
    private PostService postService;

    @Test
    void createDraft() {
        // given
        CreatePostRequest request = new CreatePostRequest(
                new TranslationRequest(
                        "Test Title",
                        "Test Summary",
                        "Test Content"),
                new TranslationRequest(
                        "Título de teste",
                        "Resumo de teste",
                        "Conteúdo de teste"));

        given(postService.createDraft(request))
                .willReturn(1L);

        String requestBody = """
                {
                    "en": {
                        "title": "Test Title",
                        "summary": "Test Summary",
                        "content": "Test Content"
                    },
                    "pt": {
                        "title": "Título de teste",
                        "summary": "Resumo de teste",
                        "content": "Conteúdo de teste"
                    }
                }
                """;

        // when
        MvcTestResult result = mockMvc.post()
                .uri("/api/v1/admin/posts")
                .contentType("application/json")
                .content(requestBody)
                .exchange();

        // then
        assertThat(result).hasStatus(HttpStatus.CREATED);
        assertThat(result).bodyJson().extractingPath("$.id").isEqualTo(1);
    }

    @Test
    void emptyTitleShouldReturnBadRequest() {
        // given
        String requestBody = """
                {
                    "en": {
                        "title": "",
                        "summary": "Test Summary",
                        "content": "Test Content"
                    },
                    "pt": {
                        "title": "Título de teste",
                        "summary": "Resumo de teste",
                        "content": "Conteúdo de teste"
                    }
                }
                """;

        // when
        MvcTestResult result = mockMvc.post()
                .uri("/api/v1/admin/posts")
                .contentType("application/json")
                .content(requestBody)
                .exchange();

        // then
        assertThat(result).hasStatus(HttpStatus.BAD_REQUEST);
    }

    @Test
    void missingLanguageShouldReturnBadRequest() {
        // given
        String requestBody = """
                {
                    "en": {
                        "title": "",
                        "summary": "Test Summary",
                        "content": "Test Content"
                    }
                }
                """;

        // when
        MvcTestResult result = mockMvc.post()
                .uri("/api/v1/admin/posts")
                .contentType("application/json")
                .content(requestBody)
                .exchange();

        // then
        assertThat(result).hasStatus(HttpStatus.BAD_REQUEST);
    }

    @Test
    void titleWithMoreThan255CharactersShouldReturnBadRequest() {
        // given
        String longTitle = "A".repeat(256);
        String requestBody = String.format("""
                {
                    "en": {
                        "title": "%s",
                        "summary": "Test Summary",
                        "content": "Test Content"
                    },
                    "pt": {
                        "title": "Título de teste",
                        "summary": "Resumo de teste",
                        "content": "Conteúdo de teste"
                    }
                }
                """, longTitle);

        // when
        MvcTestResult result = mockMvc.post()
                .uri("/api/v1/admin/posts")
                .contentType("application/json")
                .content(requestBody)
                .exchange();

        // then
        assertThat(result).hasStatus(HttpStatus.BAD_REQUEST);
    }

    @Test
    void publishPost() {
        // given
        Long postId = 1L;

        // when
        MvcTestResult result = mockMvc.post()
                .uri("/api/v1/admin/posts/{postId}/publish", postId)
                .exchange();

        // then
        assertThat(result).hasStatus(HttpStatus.NO_CONTENT);
    }

    @Test
    void publishPostWithoutTranslationShouldReturnUnprocessableContent() {
        // given
        Long postId = 1L;

        willThrow(new CannotPublishWithoutTranslationException("EN and PT"))
                .given(postService)
                .publish(postId);

        // when
        MvcTestResult result = mockMvc.post()
                .uri("/api/v1/admin/posts/{postId}/publish", postId)
                .exchange();

        // then
        assertThat(result).hasStatus(HttpStatus.UNPROCESSABLE_CONTENT);
    }

    @Test
    void publishWithAnInvalidPostIdShouldReturnNotFound() {
        // given
        Long postId = 999L;

        willThrow(new PostNotFoundException(postId))
                .given(postService)
                .publish(postId);

        // when
        MvcTestResult result = mockMvc.post()
                .uri("/api/v1/admin/posts/{postId}/publish", postId)
                .exchange();

        // then
        assertThat(result).hasStatus(HttpStatus.NOT_FOUND);
    }

    @Test
    void schedulePostAlreadyPublishedShouldReturnUnprocessableContent() {
        // given
        Long postId = 1L;
        Instant when = PUBLISHED_AT.plusSeconds(3600);

        willThrow(new CannotSchedulePublishedPostException())
                .given(postService)
                .schedule(postId, when);

        // when
        MvcTestResult result = mockMvc.post()
                .uri("/api/v1/admin/posts/{postId}/schedule", postId)
                .contentType("application/json")
                .content(String.format("""
                        {
                            "when": "%s"
                        }
                        """, when))
                .exchange();

        // then
        assertThat(result).hasStatus(HttpStatus.CONFLICT);
    }

    @Test 
    void schedulePostWithoutPublishAt() {
        // given
        Long postId = 1L;

        // when
        MvcTestResult result = mockMvc.post()
                .uri("/api/v1/admin/posts/{postId}/schedule", postId)
                .contentType("application/json")
                .content("""
                        {
                        }
                        """)
                .exchange();

        // then
        assertThat(result).hasStatus(HttpStatus.BAD_REQUEST);
    }

    @Test 
    void duplicateSlugShouldReturnConflict() {
        // given
        Long postId = 1L;
        Instant when = PUBLISHED_AT.plusSeconds(3600);

        willThrow(new SlugAlreadyExistsException())
                .given(postService)
                .schedule(postId, when);

        // when
        MvcTestResult result = mockMvc.post()
                .uri("/api/v1/admin/posts/{postId}/schedule", postId)
                .contentType("application/json")
                .content(String.format("""
                        {
                            "when": "%s"
                        }
                        """, when))
                .exchange();

        // then
        assertThat(result).hasStatus(HttpStatus.CONFLICT);
    }

}
