package com.chas.blog.repository;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Page;

import com.chas.blog.TestcontainersConfiguration;
import com.chas.blog.model.post.Language;
import com.chas.blog.model.post.Post;
import com.chas.blog.model.post.PostTranslation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThatCode;



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
        post1.addTranslation(
            Language.PT,
            "Título de teste 1",
            "titulo-de-teste-1",
            "Resumo de teste 1",
            "Conteúdo de teste 1"
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
        post2.addTranslation(
            Language.PT,
            "Título de teste 2",
            "titulo-de-teste-2",
            "Resumo de teste 2",
            "Conteúdo de teste 2"
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

    @Test
    void doesNotFindUnpublishedPosts() {

        // given 
        Post post1 = Post.createDraft(null);
        post1.addTranslation(
            Language.EN,
            "Test Title 1",
            "test-slug-1",
            "Test Summary 1",
            "Test Content 1"
        );
        post1.addTranslation(
            Language.PT,
            "Título de teste 1",
            "titulo-de-teste-1",
            "Resumo de teste 1",
            "Conteúdo de teste 1"
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
        post2.addTranslation(
            Language.PT,
            "Título de teste 2",
            "titulo-de-teste-2",
            "Resumo de teste 2",
            "Conteúdo de teste 2"
        );

        postRepository.saveAndFlush(post1);
        postRepository.saveAndFlush(post2);


        // when
        Page<PostTranslation> found = postRepository.findByLanguage(
            Language.EN, 
            NOW, 
            PageRequest.of(0, 10)
        );

         // then
        assertThat(found.getTotalElements()).isEqualTo(1);
        assertThat(found.getContent().get(0).getTitle()).isEqualTo("Test Title 1");
        assertThat(found.getContent().get(0).getPost().getId()).isEqualTo(post1.getId());

    }
    
    @Test 
    void doesNotFindPostsWithFuturePublishDate() {

        // given 
        Post post1 = Post.createDraft(null);
        post1.addTranslation(
            Language.EN,
            "Test Title 1",
            "test-slug-1",
            "Test Summary 1",
            "Test Content 1"
        );
        post1.addTranslation(
            Language.PT,
            "Título de teste 1",
            "titulo-de-teste-1",
            "Resumo de teste 1",
            "Conteúdo de teste 1"
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
        post2.addTranslation(
            Language.PT,
            "Título de teste 2",
            "titulo-de-teste-2",
            "Resumo de teste 2",
            "Conteúdo de teste 2"
        );
        post2.publish(NOW.plus(1, ChronoUnit.DAYS));

        postRepository.saveAndFlush(post1);
        postRepository.saveAndFlush(post2);

        // when
        Page<PostTranslation> found = postRepository.findByLanguage(
            Language.EN, 
            NOW, 
            PageRequest.of(0, 10)
        );


        // then
        assertThat(found.getTotalElements()).isEqualTo(1);
        assertThat(found.getContent().get(0).getTitle()).isEqualTo("Test Title 1");
        assertThat(found.getContent().get(0).getPost().getId()).isEqualTo(post1.getId());

    }


    @ParameterizedTest
    @ValueSource(strings = {
        "",
        "test slug with spaces",
        "Test-Slug",
        "slug-com-ç",
        "-starts-with-hyphen",
        "double--hyphen"
    })

    void doesNotSavePostWithInvalidSlug(String invalidSlug) {
        // given
        Post post = Post.createDraft(null);
        post.addTranslation(
            Language.EN, 
            "Test Title", 
            invalidSlug, 
            "Test Summary", 
            "Test Content"
        );

        // when / then
        assertThatThrownBy(() -> postRepository.saveAndFlush(post))
            .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test 
    void doesNotSavePostWithDuplicateSlug() {
        // given
        Post post1 = Post.createDraft(null);
        post1.addTranslation(
            Language.EN, 
            "Test Title 1", 
            "test-slug", 
            "Test Summary 1", 
            "Test Content 1"
        );
        postRepository.saveAndFlush(post1);

        Post post2 = Post.createDraft(null);
        post2.addTranslation(
            Language.EN, 
            "Test Title 2", 
            "test-slug", 
            "Test Summary 2", 
            "Test Content 2"
        );

        // when / then
        assertThatThrownBy(() -> postRepository.saveAndFlush(post2))
            .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void savesPostsWithSameSlugInDifferentLanguages() {
        // given
        Post post1 = Post.createDraft(null);
        post1.addTranslation(
            Language.EN, 
            "Test Title 1", 
            "test-slug", 
            "Test Summary 1", 
            "Test Content 1"
        );
        postRepository.saveAndFlush(post1);

        Post post2 = Post.createDraft(null);
        post2.addTranslation(
            Language.PT, 
            "Test Title 2", 
            "test-slug", 
            "Test Summary 2", 
            "Test Content 2"
        );

        // when / then
        assertThatCode(() -> postRepository.saveAndFlush(post2))
            .doesNotThrowAnyException();
        
    }

}
