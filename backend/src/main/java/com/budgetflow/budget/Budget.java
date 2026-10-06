package com.budgetflow.budget;

import com.budgetflow.category.Category;
import com.budgetflow.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

/**
 * Budget mensuel fixé pour une catégorie donnée (ex : 400 € de courses en mars 2026).
 * Le mois est stocké en clair (year + month) pour des requêtes simples.
 */
@Entity
@Table(name = "budgets",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "category_id", "year", "month"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Budget {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Column(name = "year", nullable = false)
    private int year;

    @Column(name = "month", nullable = false)
    private int month;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
