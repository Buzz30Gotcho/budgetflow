package com.budgetflow.budget.dto;

import java.math.BigDecimal;

/** Budget d'une catégorie avec sa progression (dépensé / limite). */
public record BudgetResponse(
        Long id,
        Long categoryId,
        String categoryName,
        String categoryColor,
        BigDecimal amount,
        BigDecimal spent,
        BigDecimal remaining,
        int percentage,
        int year,
        int month
) {}
