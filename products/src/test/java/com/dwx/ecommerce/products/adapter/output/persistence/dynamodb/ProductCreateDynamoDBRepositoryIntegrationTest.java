package com.dwx.ecommerce.products.adapter.output.persistence.dynamodb;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBAsync;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.dwx.configs.LocalstackInitializer;
import com.dwx.ecommerce.products.adapter.output.persistence.config.RepositoryInitializer;
import com.dwx.ecommerce.products.adapter.output.persistence.model.Product;
import com.dwx.ecommerce.products.config.aws.AwsInitializer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import reactor.test.StepVerifier;

import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ContextConfiguration(classes = {
        AwsInitializer.class,
        LocalstackInitializer.class,
        RepositoryInitializer.class
})
class ProductCreateDynamoDBRepositoryIntegrationTest {
    @Autowired
    ProductCreateDynamoDBRepository sut;
    @Autowired
    AmazonDynamoDBAsync amazonDynamoDBAsync;

    @Test
    void shouldThrowErrorWhenCidAlreadyExists() {
        final var cid = UUID.randomUUID().toString();

        final var idempotencyRegister = Map.of(
                "PK", new AttributeValue().withS("IDEMPOTENCY#" + cid),
                 "SK", new AttributeValue(cid)
        );

        amazonDynamoDBAsync.putItem("Products", idempotencyRegister);

        StepVerifier.create(
                        sut.create(
                                cid,
                                Product.builder()
                                        .code("COD1")
                                        .build()
                        )
                )
                .consumeErrorWith(thrown -> assertThat(thrown).isNotNull())
                .verify();

    }


}