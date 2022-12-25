package com.dwx.ecommerce.products.adapter.output.persistence.core.command;

public interface WriteOperation {
    String getIdentity();
    Object getOperation();
}
