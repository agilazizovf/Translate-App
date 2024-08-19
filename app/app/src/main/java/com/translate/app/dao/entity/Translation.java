package com.translate.app.dao.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "translation")
@Data
@NoArgsConstructor
public class Translation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String sourceLanguage;
    private String targetLanguage;
    private String sourceText;
    private String targetText;

    @OneToMany(mappedBy = "translation", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Comment> comments = new HashSet<>();

    private LocalDateTime translatedAt;

    @ElementCollection
    @CollectionTable(name = "translation_likes", joinColumns = @JoinColumn(name = "translation_id"))
    @Column(name = "user_id")
    private Set<Integer> likedByUsers = new HashSet<>();

    @ElementCollection
    @CollectionTable(name = "translation_dislikes", joinColumns = @JoinColumn(name = "translation_id"))
    @Column(name = "user_id")
    private Set<Integer> dislikedByUsers = new HashSet<>();

    @ElementCollection
    @CollectionTable(name = "translation_views", joinColumns = @JoinColumn(name = "translation_id"))
    @Column(name = "user_id")
    private Set<Integer> viewedByUsers = new HashSet<>();


    private Integer likes;
    private Integer dislikes;
    private Integer views;

    public Translation(String sourceLanguage, String targetLanguage,
                             String sourceText, String targetText) {
        this.sourceLanguage = sourceLanguage;
        this.targetLanguage = targetLanguage;
        this.sourceText = sourceText;
        this.targetText = targetText;
        this.translatedAt = LocalDateTime.now();
    }


}
