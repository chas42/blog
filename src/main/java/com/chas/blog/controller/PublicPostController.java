package com.chas.blog.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.chas.blog.dto.PostSummary;
import com.chas.blog.dto.PostView;
import com.chas.blog.model.post.Language;
import com.chas.blog.service.PostService;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api/v1/posts")
public class PublicPostController {

    private static final int MAX_PAGE_SIZE = 50;

    private final PostService postService;

    PublicPostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping    
    public PagedModel<PostSummary> listPosts(@RequestParam Language lang, @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, Math.min(size, MAX_PAGE_SIZE));
        return new PagedModel<>(postService.listPublished(lang, pageable));
    }

    @GetMapping("/{slug}")
    public PostView getPost(@PathVariable String slug, @RequestParam Language lang) {
        return postService.findPublished(lang, slug);
    }

}
