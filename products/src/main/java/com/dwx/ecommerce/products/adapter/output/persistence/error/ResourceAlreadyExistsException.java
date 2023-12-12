package com.dwx.ecommerce.products.adapter.output.persistence.error;

import com.dwx.ecommerce.products.adapter.output.persistence.core.error.Error;
import lombok.Getter;


@Getter
public class ResourceAlreadyExistsException extends PersistenceException{
    private static final String ERROR_CODE = Error.RESOURCE_ALREADY_EXIST.getCode();
    public ResourceAlreadyExistsException(String message) {
        super(ERROR_CODE, message, null);
    }

    public ResourceAlreadyExistsException(String message, Throwable thrown) {
        super(ERROR_CODE, message, thrown);
    }
}
