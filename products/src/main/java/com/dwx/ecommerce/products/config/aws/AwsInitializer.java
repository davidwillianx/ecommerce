package com.dwx.ecommerce.products.config.aws;

import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;


@Configuration
@RequiredArgsConstructor
public class AwsInitializer {
    private final AwsProperties awsProperties;

    public AmazonDynamoDB amazonDynamoDB() {
      return  AmazonDynamoDBClientBuilder
                .standard()
                .withEndpointConfiguration(
                        new AwsClientBuilder.EndpointConfiguration(
                                awsProperties.dynamo().endpoint(),
                                awsProperties.region()
                        )
                )
                .build();
    }

}
