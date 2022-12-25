package com.dwx.ecommerce.products.adapter.output.persistence.core.error;

public class UnexpectedProviderBehaviorException extends RuntimeException {
    private String code;

    public UnexpectedProviderBehaviorException(String code, String message, Throwable thrown) {
        super(message, thrown);
        this.code = code;
    }
}
