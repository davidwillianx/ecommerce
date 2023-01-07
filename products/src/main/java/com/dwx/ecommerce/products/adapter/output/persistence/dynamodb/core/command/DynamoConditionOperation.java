package com.dwx.ecommerce.products.adapter.output.persistence.dynamodb.core.command;

import com.amazonaws.services.dynamodbv2.model.ConditionCheck;
import com.dwx.ecommerce.products.adapter.output.persistence.core.command.Operation;
import com.dwx.ecommerce.products.adapter.output.persistence.core.command.OperationType;
import lombok.*;

import java.util.function.Function;

@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class DynamoConditionOperation implements Operation {
    private String identity;
    private ConditionCheck operation;
    private Function<RuntimeException, Throwable> errorConverter;

    @Override
    public OperationType getType() {
        return null;
    }
}
