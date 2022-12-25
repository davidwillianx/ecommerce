package com.dwx.configs;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBAsync;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBAsyncClientBuilder;
import com.amazonaws.services.dynamodbv2.model.*;
import com.dwx.ecommerce.products.config.aws.AwsProperties;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@EnableConfigurationProperties({
        AwsProperties.class
})
@EnableAutoConfiguration
@TestConfiguration
public class LocalstackInitializer {

    @Bean
    public AmazonDynamoDBAsync amazonDynamoDB(final AwsProperties awsProperties) {
        final var basicCredentials = new BasicAWSCredentials("LOCALSTACK_KEY", "LOCALSTACK_SECRET");
        final var dynamoAsync = AmazonDynamoDBAsyncClientBuilder
                .standard()
                .withCredentials(new AWSStaticCredentialsProvider(basicCredentials))
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(
                        awsProperties.dynamo().endpoint(),
                        awsProperties.region()
                )).build();

        deleteTables(dynamoAsync);
        setUp(dynamoAsync);

        return dynamoAsync;
    }

    private void deleteTables(final AmazonDynamoDBAsync dynamoDB) {
        final var deleteTable = new DeleteTableRequest()
                .withTableName("Products");

        try {
            dynamoDB.deleteTable(deleteTable);
        }catch (Exception ignored) {}

    }

    private void setUp(final AmazonDynamoDBAsync dynamoDB) {
        final var productTableRequest = new CreateTableRequest()
                .withTableName("Products")
                .withAttributeDefinitions(
                        new AttributeDefinition("PK", ScalarAttributeType.S),
                        new AttributeDefinition("SK", ScalarAttributeType.S)
                )
                .withKeySchema(
                        new KeySchemaElement("PK", KeyType.HASH),
                        new KeySchemaElement("SK", KeyType.RANGE)
                )
                .withProvisionedThroughput(new ProvisionedThroughput(10L, 10L));
        dynamoDB.createTable(productTableRequest);
    }
}
