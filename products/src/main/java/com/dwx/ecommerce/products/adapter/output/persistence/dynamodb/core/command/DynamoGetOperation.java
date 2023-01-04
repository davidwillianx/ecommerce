package com.dwx.ecommerce.products.adapter.output.persistence.dynamodb.core.command;

import com.amazonaws.services.dynamodbv2.model.Get;
import com.dwx.ecommerce.products.adapter.output.persistence.core.command.Operation;
import com.dwx.ecommerce.products.adapter.output.persistence.dynamodb.core.DynamoOperationType;
import lombok.*;


@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class DynamoGetOperation  implements Operation<Get> {
    private DynamoOperationType type = DynamoOperationType.SELECT;
    private String identity;
    private Get operation;
}
