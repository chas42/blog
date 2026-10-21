package com.chas.blog.model.post;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "posts")
@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class Post {

    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private PostStatus status;

    private Instant publishedAt;

    private Long authorId;

    @CreationTimestamp
    private Instant createdAt;

    @UpdateTimestamp
    private Instant updatedAt;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostTranslation> translations = new ArrayList<>();

    public void addTranslation(Language language, String title, String slug, String summary, String content) {
        translations.add(new PostTranslation(this ,language, title, slug, summary, content));
    }

    public static Post createDraft(Long authorId) {
        Post post = new Post();
        post.status = PostStatus.DRAFT;
        post.authorId = authorId;
        return post;
    }

    public void publish() {
        this.status = PostStatus.PUBLISHED;
        this.publishedAt = Instant.now();
    }

}
