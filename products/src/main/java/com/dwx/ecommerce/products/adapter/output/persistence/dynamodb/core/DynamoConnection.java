package com.dwx.ecommerce.products.adapter.output.persistence.dynamodb.core;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBAsync;
import com.dwx.ecommerce.products.adapter.output.persistence.core.DbConnection;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DynamoConnection implements DbConnection<AmazonDynamoDBAsync> {
    private final AmazonDynamoDBAsync connection;
    @Override
    public AmazonDynamoDBAsync get() {
        return connection;
    }
}
