package com.budgetflow.dashboard;

import com.budgetflow.dashboard.dto.DashboardSummary;
import com.budgetflow.transaction.TransactionRepository;
import com.budgetflow.transaction.TransactionType;
import com.budgetflow.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private static final int TREND_MONTHS = 6;

    private final TransactionRepository transactionRepository;

    @Transactional(readOnly = true)
    public DashboardSummary summary(User user, int year, int month) {
        Long userId = user.getId();
        YearMonth period = YearMonth.of(year, month);
        LocalDate from = period.atDay(1);
        LocalDate to = period.atEndOfMonth();

        BigDecimal income = transactionRepository.sumByTypeAndPeriod(userId, TransactionType.INCOME, from, to);
        BigDecimal expense = transactionRepository.sumByTypeAndPeriod(userId, TransactionType.EXPENSE, from, to);

        List<DashboardSummary.CategorySlice> byCategory =
                transactionRepository.sumByCategory(userId, TransactionType.EXPENSE, from, to)
                        .stream()
                        .map(p -> new DashboardSummary.CategorySlice(p.getName(), p.getColor(), p.getTotal()))
                        .toList();

        List<DashboardSummary.MonthlyPoint> trend = new ArrayList<>();
        for (int i = TREND_MONTHS - 1; i >= 0; i--) {
            YearMonth m = period.minusMonths(i);
            LocalDate mFrom = m.atDay(1);
            LocalDate mTo = m.atEndOfMonth();
            BigDecimal mIncome = transactionRepository.sumByTypeAndPeriod(userId, TransactionType.INCOME, mFrom, mTo);
            BigDecimal mExpense = transactionRepository.sumByTypeAndPeriod(userId, TransactionType.EXPENSE, mFrom, mTo);
            trend.add(new DashboardSummary.MonthlyPoint(m.toString(), mIncome, mExpense));
        }

        return new DashboardSummary(income, expense, income.subtract(expense), byCategory, trend);
    }
}
