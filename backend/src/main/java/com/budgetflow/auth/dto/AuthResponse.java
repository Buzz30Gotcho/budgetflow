package com.budgetflow.auth.dto;

/** Réponse renvoyée après une inscription ou une connexion réussie. */
public record AuthResponse(
        String token,
        Long userId,
        String firstName,
        String email
) {}
