package com.budgetflow.budget.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record BudgetRequest(
        @NotNull(message = "La catégorie est obligatoire")
        Long categoryId,

        @NotNull(message = "Le montant est obligatoire")
        @DecimalMin(value = "0.01", message = "Le montant doit être positif")
        BigDecimal amount,

        @NotNull @Min(2000) @Max(2100)
        Integer year,

        @NotNull @Min(1) @Max(12)
        Integer month
) {}
