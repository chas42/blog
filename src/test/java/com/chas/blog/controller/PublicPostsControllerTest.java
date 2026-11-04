package com.chas.blog.controller;

import java.time.Instant;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.assertj.MvcTestResult;

import com.chas.blog.dto.PostView;
import com.chas.blog.exceptions.PostNotFoundException;
import com.chas.blog.model.post.Language;
import com.chas.blog.security.SecurityConfig;
import com.chas.blog.service.PostService;

import lombok.ToString;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@WebMvcTest
@Import(SecurityConfig.class)
public class PublicPostsControllerTest {

    private static final Instant PUBLISHED_AT = Instant.parse("2026-10-20T12:00:00Z");

    @Autowired
    private MockMvcTester mockMvc;

    @MockitoBean
    private PostService postService;

    @Test
    void returnsPublishedPost() {
        // given
        given(postService.findPublished(Language.EN, "hello-world"))
                .willReturn(new PostView(
                        "hello-world",
                        "Hello World",
                        "Summary",
                        "Content",
                        Language.EN,
                        PUBLISHED_AT,
                        Map.of()));

        MvcTestResult result = mockMvc.get()
                .uri("/api/v1/posts/hello-world")
                .param("lang", "en")
                .exchange();

        assertThat(result).hasStatus(200);
        assertThat(result).bodyJson().extractingPath("$.slug").isEqualTo("hello-world");
        assertThat(result).bodyJson().extractingPath("$.title").isEqualTo("Hello World");
        assertThat(result).bodyJson().extractingPath("$.summary").isEqualTo("Summary");
        assertThat(result).bodyJson().extractingPath("$.content").isEqualTo("Content");
        assertThat(result).bodyJson().extractingPath("$.language").isEqualTo("en");
    }

    @Test
    void returns404WhenPostNotFound() {

        // given
        given(postService.findPublished(Language.EN, "non-existent")).willThrow(PostNotFoundException.class);

        // when
        MvcTestResult result = mockMvc.get()
                .uri("/api/v1/posts/non-existent")
                .param("lang", "en")
                .exchange();

        assertThat(result).hasStatus(HttpStatus.NOT_FOUND)
                .hasContentType(MediaType.APPLICATION_PROBLEM_JSON)
                .bodyJson().extractingPath("$.title").isEqualTo("Post not found");

    }

}
