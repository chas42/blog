package com.chas.blog.controller;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.assertj.MvcTestResult;

import com.chas.blog.dto.PostSummary;
import com.chas.blog.dto.PostView;
import com.chas.blog.exceptions.PostNotFoundException;
import com.chas.blog.model.post.Language;
import com.chas.blog.security.SecurityConfig;
import com.chas.blog.service.PostService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

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

    @Test
    void return400WhenLanguageIsNotsuported() {

        MvcTestResult result = mockMvc.get()
                .uri("/api/v1/posts/hello-world")
                .param("lang", "fr")
                .exchange();

        assertThat(result).hasStatus(HttpStatus.BAD_REQUEST);
        then(postService).shouldHaveNoInteractions();

    }

    @Test
    void return400WhenLanguageIsMissing() {

        MvcTestResult result = mockMvc.get()
                .uri("/api/v1/posts/hello-world")
                .exchange();

        assertThat(result).hasStatus(HttpStatus.BAD_REQUEST);
        then(postService).shouldHaveNoInteractions();
    }

    @Test
    void listPublishedPosts() {

        // given
        Pageable pageable = PageRequest.of(0, 20);
        List<PostSummary> posts = List.of(
                new PostSummary("first-post", "First Post", "Summary", PUBLISHED_AT),
                new PostSummary("second-post", "Second Post", "Summary 2", PUBLISHED_AT.plusSeconds(3600)));
        given(postService.listPublished(Language.EN, pageable)).willReturn(new PageImpl<>(posts, pageable, posts.size()));

        // when
        MvcTestResult result = mockMvc.get()
                .uri("/api/v1/posts")
                .param("lang", "en")
                .exchange();

        assertThat(result).hasStatusOk();
        assertThat(result).bodyJson().extractingPath("$.content").asArray().hasSize(2);
        assertThat(result).bodyJson().extractingPath("$.content[0].slug").isEqualTo("first-post");
        assertThat(result).bodyJson().extractingPath("$.content[1].slug").isEqualTo("second-post");
        assertThat(result).bodyJson().extractingPath("$.page.size").isEqualTo(20);
        assertThat(result).bodyJson().extractingPath("$.page.totalElements").isEqualTo(2);
        assertThat(result).bodyJson().extractingPath("$.page.totalPages").isEqualTo(1);
        assertThat(result).bodyJson().extractingPath("$.page.number").isEqualTo(0);

    }

    @Test
    void limitsPageSizeTo50() {
        // given
        given(postService.listPublished(eq(Language.EN), any(Pageable.class)))
                .willReturn(Page.empty());

        // when
        mockMvc.get()
                .uri("/api/v1/posts")
                .param("lang", "en")
                .param("size", "1000")
                .exchange();

        // then
        ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
        then(postService).should().listPublished(eq(Language.EN), captor.capture());
        assertThat(captor.getValue().getPageSize()).isEqualTo(50);
    }

}
