package com.chas.blog.controller;

import java.time.Instant;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<CreatePostResponse> createDraft(@RequestBody CreatePostRequest request) {
        Long postId = postService.createDraft(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(new CreatePostResponse(postId));
    }

    @PostMapping("/{postId}/publish")
    public ResponseEntity<Void> publishPost(@PathVariable Long postId) {
        postService.publish(postId);

        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

    @PostMapping("/{postId}/schedule")
    public ResponseEntity<Void> schedulePost(@PathVariable Long postId, @RequestBody String scheduledDateTime) {
        postService.schedule(postId, Instant.parse(scheduledDateTime));
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

}
