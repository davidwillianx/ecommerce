package com.dwx.ecommerce.products.adapter.output.persistence.error;

import lombok.Getter;

@Getter
public class UnexpectedOperationException extends PersistenceException{
    public UnexpectedOperationException(String code, String message) {
        super(code, message, null);
    }
}
