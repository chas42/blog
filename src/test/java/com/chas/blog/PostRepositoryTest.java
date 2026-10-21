package com.chas.blog;

import java.time.Instant;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.context.annotation.Import;

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

    

}
