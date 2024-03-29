package com.dwx.ecommerce.products.config.aws;


import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.dwx.ecommerce.products.adapter.config.aws.AwsInitializer;
import com.dwx.ecommerce.products.adapter.config.aws.AwsProperties;
import com.dwx.ecommerce.products.adapter.config.aws.DynamoDBProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.verify;

class AwsInitializerTest {
    AwsInitializer sut;

    AwsProperties awsProperties;

    @BeforeEach
    void setUp() {
        awsProperties = Mockito.mock(AwsProperties.class);
        sut = new AwsInitializer();
    }

    @Test
    void shouldCreateDynamoDbInstance() {
        final var dynamoEndpoint = "http://my-url.com";
        final var dynamoProperty = Mockito.mock(DynamoDBProperties.class);

        BDDMockito.given(awsProperties.dynamo()).willReturn(dynamoProperty);
        BDDMockito.given(dynamoProperty.endpoint()).willReturn(dynamoEndpoint);
        BDDMockito.given(awsProperties.region()).willReturn("us-east-1");

        final var result = sut.amazonDynamoDB(awsProperties);

        assertThat(result).isInstanceOf(AmazonDynamoDB.class);
    }

    @Test
    void shouldHaveSpecifiedProperties() {
        final var dynamoEndpoint = "http://my-url.com";
        final var dynamoProperty = Mockito.mock(DynamoDBProperties.class);

        BDDMockito.given(awsProperties.dynamo()).willReturn(dynamoProperty);
        BDDMockito.given(dynamoProperty.endpoint()).willReturn(dynamoEndpoint);
        BDDMockito.given(awsProperties.region()).willReturn("us-east-1");

        sut.amazonDynamoDB(awsProperties);

        verify(awsProperties).region();
        verify(awsProperties).dynamo();
        verify(dynamoProperty).endpoint();
    }

}