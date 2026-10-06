package com.budgetflow.dashboard.dto;

import java.math.BigDecimal;
import java.util.List;

public record DashboardSummary(
        BigDecimal totalIncome,
        BigDecimal totalExpense,
        BigDecimal balance,
        List<CategorySlice> expenseByCategory,
        List<MonthlyPoint> monthlyTrend
) {
    public record CategorySlice(String name, String color, BigDecimal total) {}

    public record MonthlyPoint(String month, BigDecimal income, BigDecimal expense) {}
}
