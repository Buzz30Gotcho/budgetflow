package com.budgetflow.transaction.dto;

import com.budgetflow.transaction.TransactionType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public record TransactionRequest(
        @NotNull(message = "Le type est obligatoire")
        TransactionType type,

        @NotNull(message = "Le montant est obligatoire")
        @DecimalMin(value = "0.01", message = "Le montant doit être positif")
        BigDecimal amount,

        String description,

        @NotNull(message = "La date est obligatoire")
        LocalDate date,

        Long categoryId
) {}
