package com.dwx.ecommerce.products.adapter.output.persistence.config;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBAsync;
import com.dwx.ecommerce.products.adapter.output.persistence.core.DbConnection;
import com.dwx.ecommerce.products.adapter.output.persistence.dynamodb.ProductCreateDynamoDBRepository;
import com.dwx.ecommerce.products.adapter.output.persistence.dynamodb.ProductCreateRepository;
import com.dwx.ecommerce.products.adapter.output.persistence.dynamodb.core.DynamoConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RepositoryInitializer {

    @Bean
    DbConnection dbConnection(final AmazonDynamoDBAsync amazonDynamoDBAsync) {
        return new DynamoConnection(amazonDynamoDBAsync);
    }

    @Bean
    ProductCreateRepository productCreateRepository(final DbConnection dbConnection) {
        return new ProductCreateDynamoDBRepository(dbConnection);
    }
}
