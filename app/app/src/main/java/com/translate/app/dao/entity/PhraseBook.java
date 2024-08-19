package com.translate.app.dao.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;


@Entity
@Data
@AllArgsConstructor
@Table(name = "phrasebooks")
@NoArgsConstructor
public class PhraseBook {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @OneToMany(mappedBy = "phraseBook")
    private Set<Phrase> phrases;
}
