package com.chas.blog.service;

import java.time.Instant;

import org.hibernate.query.Page;
import org.springframework.data.domain.Pageable;

import com.chas.blog.dto.CreatePostRequest;
import com.chas.blog.model.post.Language;

public interface PostService {

    Long createDraft(CreatePostRequest request);                // admin
    // void publish(Long postId);                                  // admin, publica agora
    // void schedule(Long postId, Instant when);                   // admin, publica no futuro
    // PostView findPublished(Language lang, String slug);         // público
    // Page<PostSummary> listPublished(Language lang, Pageable p); // público

}
