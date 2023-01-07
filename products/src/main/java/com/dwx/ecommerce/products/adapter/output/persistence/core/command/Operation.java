package com.dwx.ecommerce.products.adapter.output.persistence.core.command;

import com.amazonaws.services.dynamodbv2.model.CancellationReason;

import java.util.function.Function;

public interface Operation<O> {
    String getIdentity();
    O getOperation();

    OperationType getType();

    Function<CancellationReason, Throwable> getErrorConverter();
}
