package com.dwx.ecommerce.products.config.aws;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;

@ConfigurationProperties(prefix = "aws")
public record AwsProperties (
        String region,
        DynamoDBProperties dynamo
) {}

