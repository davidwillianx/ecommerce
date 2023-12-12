package com.dwx.ecommerce.products.adapter.output.persistence.core.error;

import lombok.Getter;

@Getter
public class UnexpectedProviderBehaviorException extends RuntimeException {
    private final String code;

    public UnexpectedProviderBehaviorException(String code, String message, Throwable thrown) {
        super(message, thrown);
        this.code = code;
    }
}
