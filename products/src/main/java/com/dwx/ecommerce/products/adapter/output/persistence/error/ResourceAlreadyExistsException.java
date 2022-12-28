package com.dwx.ecommerce.products.adapter.output.persistence.error;

import lombok.Getter;


@Getter
public class ResourceAlreadyExistsException extends PersistenceException{
    public ResourceAlreadyExistsException(String code, String message) {
        super(code, message, null);
    }

    public ResourceAlreadyExistsException(String code, String message, Throwable thrown) {
        super(code, message, thrown);
    }
}
