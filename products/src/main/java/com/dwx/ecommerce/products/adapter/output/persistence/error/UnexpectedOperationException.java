package com.dwx.ecommerce.products.adapter.output.persistence.error;

import lombok.Getter;

@Getter
public class UnexpectedOperationException extends RuntimeException{
    private String code;

    public UnexpectedOperationException(String code, String message) {
        super(message);
        this.code = code;
    }
}
