package com.dwx.ecommerce.products.adapter.output.persistence.core.error;

public class NonIdentifiableTransactionException  extends RuntimeException{
    private String code;
    public NonIdentifiableTransactionException(String code, String message) {
        super(message);
        this.code = code;
    }
}
