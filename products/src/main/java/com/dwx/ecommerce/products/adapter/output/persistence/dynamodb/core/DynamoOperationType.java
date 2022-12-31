package com.dwx.ecommerce.products.adapter.output.persistence.dynamodb.core;

import com.amazonaws.services.dynamodbv2.model.*;
import com.dwx.ecommerce.products.adapter.output.persistence.core.command.Operation;
import com.dwx.ecommerce.products.adapter.output.persistence.core.command.OperationType;
import lombok.Getter;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum DynamoOperationType implements OperationType {
    INSERT("INSERT", o -> new TransactWriteItem().withPut((Put) o.getOperation())),
    UPDATE("UPDATE", o -> new TransactWriteItem().withPut((Put) o.getOperation())),
    DELETE("DELETE", o -> new TransactWriteItem().withDelete((Delete) o.getOperation())),
    SELECT("SELECT", o -> new TransactGetItem().withGet((Get) o.getOperation()))
    ;

    private static final Map<String, Function<Operation, TransactWriteItem>> handler = Stream.of(DynamoOperationType.values())
            .collect(Collectors.toMap(DynamoOperationType::getName, DynamoOperationType::getMapper));


    @Getter
    private String name;

    @Getter
    private Function<Operation, TransactWriteItem> mapper;



    DynamoOperationType(String name, Function<Operation, TransactWriteItem> operationMapper) {
        this.name = name;
        this.mapper = operationMapper;
    }

    public static Function<Operation, TransactWriteItem> find(String name) {
        final var hasOperationName = handler.containsKey(name);

        if(hasOperationName) {
            return handler.get(name);
        }

        throw new RuntimeException();
    }

}
