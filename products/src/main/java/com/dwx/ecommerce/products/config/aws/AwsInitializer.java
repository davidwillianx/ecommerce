package com.dwx.ecommerce.products.config.aws;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBAsync;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBAsyncClientBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;


@Configuration
@RequiredArgsConstructor
public class AwsInitializer {
    @Primary
    @Bean
    public AmazonDynamoDBAsync amazonDynamoDB(final AwsProperties awsProperties) {
        final var basicCredentials = new BasicAWSCredentials("LOCALSTACK_KEY", "LOCALSTACK_SECRET");
        return AmazonDynamoDBAsyncClientBuilder
                .standard()
                .withCredentials(new AWSStaticCredentialsProvider(basicCredentials))
                .withEndpointConfiguration(
                        new AwsClientBuilder.EndpointConfiguration(
                                awsProperties.dynamo().endpoint(),
                                awsProperties.region()
                        )
                )
                .build();
    }

}
