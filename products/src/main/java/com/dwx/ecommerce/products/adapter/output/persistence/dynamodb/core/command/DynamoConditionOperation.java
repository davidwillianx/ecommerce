package com.dwx.ecommerce.products.adapter.output.persistence.dynamodb.core.command;

import com.amazonaws.services.dynamodbv2.model.ConditionCheck;
import com.dwx.ecommerce.products.adapter.output.persistence.core.command.Operation;
import com.dwx.ecommerce.products.adapter.output.persistence.core.command.OperationType;
import lombok.*;

@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class DynamoConditionOperation implements Operation {
    private String identity;
    private ConditionCheck operation;

    @Override
    public OperationType getType() {
        return null;
    }
}
