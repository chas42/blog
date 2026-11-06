package com.chas.blog.controller;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.chas.blog.dto.CreatePostRequest;
import com.chas.blog.service.PostService;

@RestController
@RequestMapping("/api/v1/admin/posts")
public class AdminPostController {

    private final PostService postService;

    public AdminPostController(PostService postService) {
        this.postService = postService;
    }

    @PostMapping
    public void createDraft(@RequestBody CreatePostRequest request) {

        postService.createDraft(null);

    }

    @PostMapping("/{postId}/publish")
    public void publishPost(@PathVariable Long postId) {

    }

    @PostMapping("/{postId}/schedule")
    public void schedulePost(@PathVariable Long postId) {

    }

}
