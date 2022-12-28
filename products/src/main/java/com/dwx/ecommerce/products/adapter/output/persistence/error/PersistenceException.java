package com.dwx.ecommerce.products.adapter.output.persistence.error;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class PersistenceException extends RuntimeException {
    private String code;
    private String message;
    private Throwable thrown;
}
