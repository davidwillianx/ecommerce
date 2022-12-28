package com.dwx.ecommerce.products.adapter.output.persistence.error;

import lombok.Getter;

@Getter
public class IdempotencyException extends PersistenceException{
    public IdempotencyException(String code, String message) {
        super(code, message, null);
    }
}
