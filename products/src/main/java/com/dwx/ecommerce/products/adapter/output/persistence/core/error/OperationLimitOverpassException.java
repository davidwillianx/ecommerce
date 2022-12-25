package com.dwx.ecommerce.products.adapter.output.persistence.core.error;

public class OperationLimitOverpassException extends RuntimeException {
    private String code;

    public OperationLimitOverpassException(String code , String message) {
        super(message);
        this.code = code;
    }
}
