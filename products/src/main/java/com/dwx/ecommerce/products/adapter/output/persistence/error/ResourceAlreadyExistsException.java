package com.dwx.ecommerce.products.adapter.output.persistence.error;

import lombok.Getter;


@Getter
public class ResourceAlreadyExistsException extends RuntimeException {
    private String code;

    public ResourceAlreadyExistsException(String code, String message) {
        super(message);
        this.code = code;
    }

}
