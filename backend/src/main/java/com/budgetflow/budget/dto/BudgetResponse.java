package com.budgetflow.budget.dto;

import java.math.BigDecimal;

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
