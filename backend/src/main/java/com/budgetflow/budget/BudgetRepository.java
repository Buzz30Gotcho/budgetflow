package com.budgetflow.budget;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BudgetRepository extends JpaRepository<Budget, Long> {
    List<Budget> findByUserIdAndYearAndMonth(Long userId, int year, int month);
    Optional<Budget> findByIdAndUserId(Long id, Long userId);
    Optional<Budget> findByUserIdAndCategoryIdAndYearAndMonth(
            Long userId, Long categoryId, int year, int month);
}
