package com.dwx.ecommerce.products.adapter.output.persistence.core.error;

public class NoTransactionOperationDefinedException extends RuntimeException{
    private String code;

    public NoTransactionOperationDefinedException(String code, String message) {}
}
