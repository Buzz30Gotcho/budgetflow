package com.budgetflow.dashboard.dto;

import java.math.BigDecimal;
import java.util.List;

/** Données agrégées affichées sur le tableau de bord. */
public record DashboardSummary(
        BigDecimal totalIncome,
        BigDecimal totalExpense,
        BigDecimal balance,
        List<CategorySlice> expenseByCategory,
        List<MonthlyPoint> monthlyTrend
) {
    /** Part d'une catégorie dans les dépenses (pour le camembert). */
    public record CategorySlice(String name, String color, BigDecimal total) {}

    /** Point mensuel revenus/dépenses (pour la courbe). */
    public record MonthlyPoint(String month, BigDecimal income, BigDecimal expense) {}
}
