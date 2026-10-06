package com.budgetflow.transaction.dto;

import com.budgetflow.transaction.Transaction;
import com.budgetflow.transaction.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDate;

public record TransactionResponse(
        Long id,
        TransactionType type,
        BigDecimal amount,
        String description,
        LocalDate date,
        Long categoryId,
        String categoryName,
        String categoryColor
) {
    public static TransactionResponse from(Transaction t) {
        var c = t.getCategory();
        return new TransactionResponse(
                t.getId(), t.getType(), t.getAmount(), t.getDescription(), t.getDate(),
                c != null ? c.getId() : null,
                c != null ? c.getName() : null,
                c != null ? c.getColor() : null);
    }
}
