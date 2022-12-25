package com.dwx.ecommerce.products.adapter.output.persistence.core.error;

public class NonUniqueIdentifierException extends RuntimeException{
    private String code;

    public NonUniqueIdentifierException(String code, String message) {
        super(message);
        this.code = code;
    }
}
