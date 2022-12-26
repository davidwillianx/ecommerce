package com.dwx.ecommerce.products.adapter.output.persistence.dynamodb.core.command;

import com.amazonaws.services.dynamodbv2.model.TransactWriteItem;
import com.dwx.ecommerce.products.adapter.output.persistence.core.command.Operation;
import lombok.*;


@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class DynamoWriteOperation implements Operation<TransactWriteItem> {
    private String identity;
    private TransactWriteItem operation;
}
