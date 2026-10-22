package com.chas.blog;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Page;

import com.chas.blog.model.post.Language;
import com.chas.blog.model.post.Post;
import com.chas.blog.model.post.PostTranslation;
import com.chas.blog.repository.PostRepository;

import static org.assertj.core.api.Assertions.assertThat;


@DataJpaTest 
@Import(TestcontainersConfiguration.class)
public class PostRepositoryTest {

    @Autowired 
    PostRepository postRepository;  

    @Autowired 
    TestEntityManager testEntityManager;

    private static final Instant NOW = Instant.parse("2026-10-07T12:00:00Z");


    @Test
    void savesPostWithTranslationAndFindsItBySlug() {
        
        // given
        Post post = Post.createDraft(null);

        post.addTranslation(
           Language.EN, 
            "Test Title", 
            "test-title", 
            "Test Summary", 
            "Test Content"
        );
        post.addTranslation(
            Language.PT, 
            "Título de teste", 
            "titulo-de-teste", 
            "Resumen de teste", 
            "Contenido de teste"
        );

        post.publish(NOW.minus(1, ChronoUnit.DAYS));


        postRepository.saveAndFlush(post);

        testEntityManager.clear();

        // when
        Optional<PostTranslation> found = postRepository
            .findPublishedSlug(Language.PT, "titulo-de-teste", NOW);

        // then
        assertThat(found).isPresent();
        assertThat(found.get().getTitle()).isEqualTo("Título de teste");
        assertThat(found.get().getSlug()).isEqualTo("titulo-de-teste");
        assertThat(found.get().getPost().getId()).isEqualTo(post.getId());

    }

    @Test
    void savesPostsWithTranslationsAndFindsThemByLanguage() {
        // given 
        Post post1 = Post.createDraft(null);
        post1.addTranslation(
            Language.EN,
            "Test Title 1",
            "test-slug-1",
            "Test Summary 1",
            "Test Content 1"
        );
        post1.publish(NOW.minus(2, ChronoUnit.DAYS));
        
        Post post2 = Post.createDraft(null);
        post2.addTranslation(
            Language.EN,
            "Test Title 2",
            "test-slug-2",
            "Test Summary 2",
            "Test Content 2"
        );
        post2.publish(NOW.minus(1, ChronoUnit.DAYS));

        postRepository.saveAndFlush(post1);
        postRepository.saveAndFlush(post2);     

        testEntityManager.clear();

        // when
        Page<PostTranslation> found = postRepository.findByLanguage(
            Language.EN, 
            NOW, 
            PageRequest.of(0, 10)
        );

        // then
        assertThat(found.getTotalElements()).isEqualTo(2);
        assertThat(found.getContent().get(0).getTitle()).isEqualTo("Test Title 2");
        assertThat(found.getContent().get(1).getTitle()).isEqualTo("Test Title 1");
        assertThat(found.getContent().get(0).getPost().getId()).isEqualTo(post2.getId());
        assertThat(found.getContent().get(1).getPost().getId()).isEqualTo(post1.getId());

    }
    
}
