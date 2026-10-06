package com.budgetflow.category.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record CategoryRequest(
        @NotBlank(message = "Le nom est obligatoire")
        String name,

        @NotBlank(message = "La couleur est obligatoire")
        @Pattern(regexp = "^#([0-9a-fA-F]{6})$", message = "Couleur hexadécimale invalide (ex : #4F46E5)")
        String color
) {}
