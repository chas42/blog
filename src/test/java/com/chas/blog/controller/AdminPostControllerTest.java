package com.chas.blog.controller;

import java.time.Instant;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.assertj.MvcTestResult;

import com.chas.blog.dto.CreatePostRequest;
import com.chas.blog.dto.TranslationRequest;
import com.chas.blog.security.SecurityConfig;
import com.chas.blog.service.PostService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

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
        assertThat(result).hasStatus(201);
        assertThat(result).bodyJson().extractingPath("$.id").isEqualTo(1);
    }

}
