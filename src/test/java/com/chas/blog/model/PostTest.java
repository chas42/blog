package com.chas.blog.model;

import org.junit.jupiter.api.Test;

import com.chas.blog.exceptions.CannotPublishWithoutTranslationException;
import com.chas.blog.model.post.Language;
import com.chas.blog.model.post.Post;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThatCode;

import java.time.Instant;


public class PostTest {

    @Test
    void doesLaunchExceptionWhenPostIsPublishedWithoutTranslation() {
        
        Post post = Post.createDraft(null);

        Instant now = Instant.now();
        
        assertThatThrownBy(() -> post.publish(now))
            .isInstanceOf(CannotPublishWithoutTranslationException.class);      

    }

    @Test
    void doesLaunchExceptionWhenPostIsPublishedWithoutTranslationInEnglish() {
        
        Post post = Post.createDraft(null);
        post.addTranslation(Language.PT, 
            "Title", 
            "slug", 
            "summary", 
            "content"
        );

        Instant now = Instant.now();
        
        assertThatThrownBy(() -> post.publish(now))
            .isInstanceOf(CannotPublishWithoutTranslationException.class);      

    }

    @Test
    void doesLaunchExceptionWhenPostIsPublishedWithoutTranslationInPortuguese() {
        
        Post post = Post.createDraft(null);
        post.addTranslation(Language.EN, 
            "Title", 
            "slug", 
            "summary", 
            "content"
        );

        Instant now = Instant.now();
        
        assertThatThrownBy(() -> post.publish(now))
            .isInstanceOf(CannotPublishWithoutTranslationException.class);      

    }

    @Test 
    void publishesPostWithTranslation() {
        
        Post post = Post.createDraft(null);
        post.addTranslation(Language.EN, 
            "Title", 
            "slug", 
            "summary", 
            "content"
        );
        post.addTranslation(Language.PT, 
            "Title", 
            "slug", 
            "summary", 
            "content"
        );

        Instant now = Instant.now();
        
        assertThatCode(() -> post.publish(now)).doesNotThrowAnyException();

    }

}
