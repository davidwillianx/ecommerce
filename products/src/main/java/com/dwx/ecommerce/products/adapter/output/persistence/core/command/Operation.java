package com.dwx.ecommerce.products.adapter.output.persistence.core.command;

public interface Operation<O> {
    String getIdentity();
    O getOperation();
}
