package com.dwx.ecommerce.products.adapter.output.persistence.dynamodb.core.command;

import com.amazonaws.services.dynamodbv2.model.Put;
import com.dwx.ecommerce.products.adapter.output.persistence.core.command.Operation;
import com.dwx.ecommerce.products.adapter.output.persistence.core.command.OperationType;
import com.dwx.ecommerce.products.adapter.output.persistence.dynamodb.core.DynamoOperationType;
import lombok.*;


@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class DynamoWriteOperation implements Operation<Put> {
    @Builder.Default
    private final OperationType type = DynamoOperationType.INSERT;
    private String identity;
    private Put operation;


}
