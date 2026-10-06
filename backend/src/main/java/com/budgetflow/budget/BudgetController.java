package com.budgetflow.budget;

import com.budgetflow.budget.dto.BudgetRequest;
import com.budgetflow.budget.dto.BudgetResponse;
import com.budgetflow.user.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.YearMonth;
import java.util.List;

@RestController
@RequestMapping("/api/budgets")
@RequiredArgsConstructor
public class BudgetController {

    private final BudgetService budgetService;

    @GetMapping
    public List<BudgetResponse> list(@AuthenticationPrincipal User user,
                                     @RequestParam(required = false) Integer year,
                                     @RequestParam(required = false) Integer month) {
        YearMonth now = YearMonth.now();
        int y = year != null ? year : now.getYear();
        int m = month != null ? month : now.getMonthValue();
        return budgetService.list(user, y, m);
    }

    @PostMapping
    public ResponseEntity<BudgetResponse> create(@AuthenticationPrincipal User user,
                                                 @Valid @RequestBody BudgetRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(budgetService.create(user, request));
    }

    @PutMapping("/{id}")
    public BudgetResponse update(@AuthenticationPrincipal User user,
                                 @PathVariable Long id,
                                 @Valid @RequestBody BudgetRequest request) {
        return budgetService.update(user, id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@AuthenticationPrincipal User user, @PathVariable Long id) {
        budgetService.delete(user, id);
        return ResponseEntity.noContent().build();
    }
}
