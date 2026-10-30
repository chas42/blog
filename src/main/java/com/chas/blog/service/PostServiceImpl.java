package com.chas.blog.service;

import java.time.Clock;
import java.time.Instant;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.chas.blog.dto.CreatePostRequest;
import com.chas.blog.dto.PostSummary;
import com.chas.blog.dto.PostView;
import com.chas.blog.exceptions.CannotSchedulePostInThePastException;
import com.chas.blog.exceptions.CannotSchedulePublishedPostException;
import com.chas.blog.exceptions.PostNotFoundException;
import com.chas.blog.model.post.Language;
import com.chas.blog.model.post.Post;
import com.chas.blog.model.post.PostStatus;
import com.chas.blog.model.post.PostTranslation;
import com.chas.blog.repository.PostRepository;
import com.chas.blog.util.SlugGenerator;


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
            Language.EN,
            request.en().title(),
            SlugGenerator.fromTitle(request.en().title()),
            request.en().summary(),
            request.en().content()
        );

        post.addTranslation(
            Language.PT,
            request.pt().title(),
            SlugGenerator.fromTitle(request.pt().title()),
            request.pt().summary(),
            request.pt().content()
        );

        Post savedPost = postRepository.save(post);

        return savedPost.getId();
    }

    @Override
    @Transactional 
    public void publish(Long postId) {

        Post postFound = postRepository.findById(postId)
            .orElseThrow(() -> new PostNotFoundException(postId));

        postFound.publish(clock.instant());
    }

    @Override
    @Transactional 
    public void schedule(Long postId, Instant when) {
    
        if (when.isBefore(clock.instant())) {
            throw new CannotSchedulePostInThePastException();
        }

        Post postFound = postRepository.findById(postId)
            .orElseThrow(() -> new PostNotFoundException(postId));

        if (postFound.getStatus() == PostStatus.PUBLISHED) {
            throw new CannotSchedulePublishedPostException();
        }

        postFound.publish(when);

    }

    @Override
    @Transactional(readOnly = true)
    public PostView findPublished(Language lang, String slug) {
        
        PostTranslation postTranslation = postRepository.findPublishedSlug(lang, slug, clock.instant())
            .orElseThrow(() -> new PostNotFoundException(slug));

        return new PostView(
            postTranslation.getTitle(), 
            postTranslation.getSummary(), 
            postTranslation.getContent(), 
            postTranslation.getSlug(), 
            postTranslation.getLanguage()
        );
        
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PostSummary> listPublished(Language lang, Pageable p) {

        Page<PostTranslation> posts = postRepository.findByLanguage(lang, clock.instant(), p);

        return posts.map(t -> new PostSummary(
            t.getTitle(),
            t.getSummary()
        ));
    }

}
