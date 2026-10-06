package com.budgetflow.category;

import com.budgetflow.user.User;
import jakarta.persistence.*;
import lombok.*;

/** Catégorie de transaction (ex : Courses, Loyer, Loisirs), propre à un utilisateur. */
@Entity
@Table(name = "categories")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    /** Couleur hexadécimale utilisée pour les graphiques (ex : #4F46E5). */
    @Column(nullable = false)
    @Builder.Default
    private String color = "#6366F1";

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
