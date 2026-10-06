package com.budgetflow.common;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/** Exception métier portant un code HTTP, traduite en réponse JSON par le handler global. */
@Getter
public class ApiException extends RuntimeException {

    private final HttpStatus status;

    public ApiException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }

    public static ApiException notFound(String message) {
        return new ApiException(HttpStatus.NOT_FOUND, message);
    }
}
