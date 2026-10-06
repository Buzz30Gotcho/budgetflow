package com.budgetflow.dashboard;

import com.budgetflow.dashboard.dto.DashboardSummary;
import com.budgetflow.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.YearMonth;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    /** Résumé du mois demandé (par défaut : le mois courant). */
    @GetMapping
    public DashboardSummary summary(@AuthenticationPrincipal User user,
                                    @RequestParam(required = false) Integer year,
                                    @RequestParam(required = false) Integer month) {
        YearMonth now = YearMonth.now();
        int y = year != null ? year : now.getYear();
        int m = month != null ? month : now.getMonthValue();
        return dashboardService.summary(user, y, m);
    }
}
