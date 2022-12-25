package com.dwx.ecommerce.products.adapter.output.persistence.dynamodb.core.error;

public class ResourceNotFoundException extends RuntimeException{
    private String code;

    public ResourceNotFoundException(String code, String message) {
        super(message);
        this.code  = code;
    }


}
