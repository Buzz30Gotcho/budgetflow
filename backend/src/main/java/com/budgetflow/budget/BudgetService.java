package com.budgetflow.budget;

import com.budgetflow.budget.dto.BudgetRequest;
import com.budgetflow.budget.dto.BudgetResponse;
import com.budgetflow.category.Category;
import com.budgetflow.category.CategoryRepository;
import com.budgetflow.common.ApiException;
import com.budgetflow.transaction.TransactionRepository;
import com.budgetflow.transaction.TransactionType;
import com.budgetflow.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BudgetService {

    private final BudgetRepository budgetRepository;
    private final CategoryRepository categoryRepository;
    private final TransactionRepository transactionRepository;

    @Transactional(readOnly = true)
    public List<BudgetResponse> list(User user, int year, int month) {
        return budgetRepository.findByUserIdAndYearAndMonth(user.getId(), year, month)
                .stream().map(b -> toResponse(user, b)).toList();
    }

    @Transactional
    public BudgetResponse create(User user, BudgetRequest request) {
        Category category = getCategory(user, request.categoryId());
        budgetRepository.findByUserIdAndCategoryIdAndYearAndMonth(
                        user.getId(), category.getId(), request.year(), request.month())
                .ifPresent(b -> {
                    throw new ApiException(HttpStatus.CONFLICT, "Un budget existe déjà pour cette catégorie ce mois-ci");
                });
        Budget budget = Budget.builder()
                .amount(request.amount())
                .year(request.year())
                .month(request.month())
                .category(category)
                .user(user)
                .build();
        return toResponse(user, budgetRepository.save(budget));
    }

    @Transactional
    public BudgetResponse update(User user, Long id, BudgetRequest request) {
        Budget budget = getOwned(user, id);
        budget.setAmount(request.amount());
        budget.setCategory(getCategory(user, request.categoryId()));
        budget.setYear(request.year());
        budget.setMonth(request.month());
        return toResponse(user, budgetRepository.save(budget));
    }

    @Transactional
    public void delete(User user, Long id) {
        budgetRepository.delete(getOwned(user, id));
    }

    private BudgetResponse toResponse(User user, Budget b) {
        YearMonth period = YearMonth.of(b.getYear(), b.getMonth());
        LocalDate from = period.atDay(1);
        LocalDate to = period.atEndOfMonth();
        BigDecimal spent = transactionRepository.sumByCategoryAndPeriod(
                user.getId(), b.getCategory().getId(), TransactionType.EXPENSE, from, to);
        BigDecimal remaining = b.getAmount().subtract(spent);
        int percentage = b.getAmount().signum() == 0 ? 0
                : spent.multiply(BigDecimal.valueOf(100))
                    .divide(b.getAmount(), 0, RoundingMode.HALF_UP).intValue();
        return new BudgetResponse(
                b.getId(), b.getCategory().getId(), b.getCategory().getName(), b.getCategory().getColor(),
                b.getAmount(), spent, remaining, percentage, b.getYear(), b.getMonth());
    }

    private Budget getOwned(User user, Long id) {
        return budgetRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> ApiException.notFound("Budget introuvable"));
    }

    private Category getCategory(User user, Long categoryId) {
        return categoryRepository.findByIdAndUserId(categoryId, user.getId())
                .orElseThrow(() -> ApiException.notFound("Catégorie introuvable"));
    }
}
