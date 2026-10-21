package com.chas.blog.repository;

import java.time.Instant;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.chas.blog.model.post.Language;
import com.chas.blog.model.post.Post;
import com.chas.blog.model.post.PostTranslation;

public interface PostRepository extends JpaRepository<Post, Long> {

    @Query("""
                SELECT t from PostTranslation t
                join fetch t.post p
                where t.language = :language
                and t.slug = :slug
                and p.publishedAt <= :now
                and p.status = 'PUBLISHED'
            """)
    Optional<PostTranslation> findPublishedSlug(Language language, String slug, Instant now);

    @Query("""
                SELECT t from PostTranslation t
                join fetch t.post p
                where t.language = :language
                and p.publishedAt <= :now
                and p.status = 'PUBLISHED'
                order by p.publishedAt desc
            """)
    Page<PostTranslation> findByLanguageAndSlug(Language language, Instant now, Pageable pageable);

}
