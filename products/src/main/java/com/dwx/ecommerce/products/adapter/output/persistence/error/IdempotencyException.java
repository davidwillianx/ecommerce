package com.dwx.ecommerce.products.adapter.output.persistence.error;

import com.dwx.ecommerce.products.adapter.output.persistence.core.error.Error;
import lombok.Getter;

@Getter
public class IdempotencyException extends PersistenceException {
    private static final Error error = Error.NON_UNIQUE_TRANSACTION;
    private static final String DEFAULT_MESSAGE = "Idempotency check already in use";

    public IdempotencyException() {
        super(
                error.getCode(),
                DEFAULT_MESSAGE,
                null
        );
    }
}
