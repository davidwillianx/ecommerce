package com.dwx.ecommerce.products.adapter.output.persistence.dynamodb;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBAsync;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.GetItemRequest;
import com.dwx.configs.LocalstackInitializer;
import com.dwx.ecommerce.products.adapter.output.persistence.config.RepositoryInitializer;
import com.dwx.ecommerce.products.adapter.output.persistence.error.IdempotencyException;
import com.dwx.ecommerce.products.adapter.config.aws.AwsInitializer;
import com.dwx.ecommerce.products.application.domain.Product;
import com.dwx.ecommerce.products.application.domain.ProductCategory;
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
    void shouldThrowErrorWhenIdempotencyCheckAlreadyExists() {
        final var externalId = UUID.randomUUID().toString();

        final var idempotencyRegister = Map.of(
                "PK", new AttributeValue().withS("IDEMPOTENCY#" + externalId),
                "SK", new AttributeValue("idempotencyCode")
        );

        amazonDynamoDBAsync.putItem("Products", idempotencyRegister);

        StepVerifier.create(
                        sut.execute(
                                externalId,
                                Product.builder()
                                        .code("idempotencyCode")
                                        .description("description")
                                        .category(ProductCategory.FURNITURE)
                                        .build()
                        )
                )
                .consumeErrorWith(thrown -> {
                    assertThat(thrown).isNotNull();
                    final var error = (IdempotencyException) thrown;
                    assertThat(error.getMessage()).isEqualTo("Idempotency check already in use");
                })
                .verify();
    }

    @Test
    void shouldExecuteSuccessfully() {
        final var trackingId = UUID.randomUUID().toString();
        final var product = Product.builder()
                .code(UUID.randomUUID().toString())
                .description("beautyAndBeast")
                .category(ProductCategory.BEAUTY)
                .build();

        StepVerifier.create(sut.execute(trackingId, product))
                .assertNext(result -> {
                    assertThat(result).isNotNull();
                    final var getRequest = new GetItemRequest();
                    getRequest.withTableName("Products");
                    getRequest.addKeyEntry("PK", new AttributeValue().withS(result.getCode()));
                    getRequest.addKeyEntry("SK", new AttributeValue().withS(result.getCategory().name()));

                    final var hasSuccessfullyPersisted = !amazonDynamoDBAsync.getItem(getRequest).getItem().isEmpty();
                    assertThat(hasSuccessfullyPersisted).isTrue();
                })
                .verifyComplete();
    }

}