package com.dwx.ecommerce.products.adapter.output.persistence.dynamodb.core;

import com.amazonaws.services.dynamodbv2.document.ItemUtils;
import com.amazonaws.services.dynamodbv2.model.Put;
import com.dwx.ecommerce.products.adapter.output.persistence.dynamodb.core.command.DynamoWriteOperation;
import com.dwx.ecommerce.products.adapter.output.persistence.dynamodb.core.domain.PK;
import com.dwx.ecommerce.products.adapter.output.persistence.error.IdempotencyException;
import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@Getter
public class IdempotencyOperationBuilder {
    private final DynamoWriteOperation operation;

    @Builder
    public IdempotencyOperationBuilder(
            String entityName,
            String transactionId,
            PK pk
    ) {
        final var persistenceIndex = ItemUtils.fromSimpleMap(Map.of(
                "PK", "IDEMPOTENCY#" + transactionId,
                "SK", pk.getPK()
        ));
        final var conditionalValues = ItemUtils.fromSimpleMap(Map.of(
                ":pk", "IDEMPOTENCY#" + transactionId,
                ":sk", pk.getPK()
        ));

        final var conditionalExpression = "PK <> :pk AND SK <> :sk";

        final var transaction = new Put()
                .withTableName(entityName)
                .withItem(persistenceIndex)
                .withConditionExpression(conditionalExpression)
                .withExpressionAttributeValues(conditionalValues);


        operation = DynamoWriteOperation.builder()
                .operation(transaction)
                .errorConverter(thrown -> new IdempotencyException())
                .identity(transactionId)
                .build();
    }
}
