package com.dwx.ecommerce.products.adapter.output.persistence.dynamodb.core.command;

import com.amazonaws.services.dynamodbv2.model.CancellationReason;
import com.amazonaws.services.dynamodbv2.model.Get;
import com.dwx.ecommerce.products.adapter.output.persistence.core.command.Operation;
import com.dwx.ecommerce.products.adapter.output.persistence.dynamodb.core.DynamoOperationType;
import lombok.*;

import java.util.function.Function;


@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class DynamoGetOperation  implements Operation<Get> {
    private DynamoOperationType type = DynamoOperationType.SELECT;
    private String identity;
    private Get operation;
    private Function<CancellationReason, Throwable> errorConverter;
}
