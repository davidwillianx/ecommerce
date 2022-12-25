package com.dwx.ecommerce.products.adapter.output.persistence.dynamodb.core.error;

public class TableNotFoundException extends RuntimeException {
    private String code;

    public TableNotFoundException(String code, String message, Throwable thrown) {
        super(message, thrown);
        this.code = code;
    }
}
