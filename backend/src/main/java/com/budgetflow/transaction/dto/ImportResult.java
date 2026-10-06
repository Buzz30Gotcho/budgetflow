package com.budgetflow.transaction.dto;

import java.util.List;

public record ImportResult(int imported, List<String> errors) {}
