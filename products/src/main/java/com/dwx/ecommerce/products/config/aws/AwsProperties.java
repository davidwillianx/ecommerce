package com.dwx.ecommerce.products.config.aws;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@ConstructorBinding
@ConfigurationProperties(prefix = "aws")
public record AwsProperties (
        String endpoint,
        String region,
        DynamoDBProperties dynamo
) {}

