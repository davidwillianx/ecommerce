package com.dwx.ecommerce.products.adapter.output.persistence.config;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBAsync;
import com.dwx.ecommerce.products.adapter.output.persistence.dynamodb.ProductCreateDynamoDBRepository;
import com.dwx.ecommerce.products.adapter.output.persistence.dynamodb.ProductCreateRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RepositoryInitializer {

    @Bean
    ProductCreateRepository productCreateRepository(final AmazonDynamoDBAsync amazonDynamoDBAsync) {
        return new ProductCreateDynamoDBRepository(amazonDynamoDBAsync);
    }
}
