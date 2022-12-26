package com.dwx.ecommerce.products.adapter.output.persistence.core.command;

public interface Operation<T> {
    String getIdentity();
    T getOperation();
}
