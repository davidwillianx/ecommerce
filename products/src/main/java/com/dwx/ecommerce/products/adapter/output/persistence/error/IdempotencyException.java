package com.dwx.ecommerce.products.adapter.output.persistence.error;

import lombok.Getter;

@Getter
public class IdempotencyException extends RuntimeException {
    private String code;

    public IdempotencyException(String code, String message) {
        super(message);
        this.code = code;
    }


}
