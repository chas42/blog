package com.chas.blog.service;

import java.time.Clock;

import org.springframework.stereotype.Service;

import com.chas.blog.dto.CreatePostRequest;
import com.chas.blog.model.post.Post;
import com.chas.blog.repository.PostRepository;
import com.chas.blog.util.SlugGenerator;

import jakarta.transaction.Transactional;

@Service
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final Clock clock;

    PostServiceImpl(PostRepository postRepository, Clock clock) {
        this.postRepository = postRepository;
        this.clock = clock;
    }

    @Override
    @Transactional 
    public Long createDraft(CreatePostRequest request) {

        Post post = Post.createDraft(request.authorId());

        post.addTranslation(
            request.language(),
            request.title(),
            SlugGenerator.fromTitle(request.title()),
            request.content(),
            request.content()
        );

        Post savedPost = postRepository.save(post);

        return savedPost.getId();
    }



}
