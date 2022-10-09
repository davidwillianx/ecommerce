package com.dwx.ecommerce.products.adapter.output.persistece.dynamodb;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBAsync;
import com.amazonaws.services.dynamodbv2.model.*;
import com.dwx.ecommerce.products.adapter.output.persistence.dynamodb.ProductCreateDynamoDBRepository;
import com.dwx.ecommerce.products.adapter.output.persistence.dynamodb.ProductCreateRepository;
import com.dwx.ecommerce.products.adapter.output.persistence.error.IdempotencyException;
import com.dwx.ecommerce.products.adapter.output.persistence.error.ResourceAlreadyExistsException;
import com.dwx.ecommerce.products.adapter.output.persistence.error.UnexpectedOperationException;
import com.dwx.ecommerce.products.adapter.output.persistence.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import reactor.test.StepVerifier;

import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ProductCreateDynamoDBRepositoryUnitTest {

    ProductCreateRepository sut;
    AmazonDynamoDBAsync dynamoDB;

    @BeforeEach
    void setUp() {
        dynamoDB = Mockito.mock(AmazonDynamoDBAsync.class);
        sut = new ProductCreateDynamoDBRepository(dynamoDB);
    }

    @Test
    void shouldThrowErrorWhenCidAlreadyInUse() {
        final var result = Mockito.mock(GetItemResult.class);
        final var cid = UUID.randomUUID().toString();
        final var product = Product.builder().build();

        BDDMockito.given(dynamoDB.getItem(Mockito.any(GetItemRequest.class)))
                .willReturn(result);

        StepVerifier.create(sut.create(cid, product))
                .consumeErrorWith(thrown -> {
                    assertThat(thrown).isInstanceOf(IdempotencyException.class);
                    final var error = (IdempotencyException) thrown;

                    assertThat(error.getCode()).isEqualTo("PROD_001");
                    assertThat(error.getMessage()).isEqualTo("Cid already in use");
                })
                .verify();
    }

    @Test
    void shouldThrowErrorCodeAlreadyExists() {
        final var cidResult = Mockito.mock(GetItemResult.class);
        final var conditionalCheckFailure = Mockito.mock(ConditionalCheckFailedException.class);
        final var cid = UUID.randomUUID().toString();
        final var product = Product.builder()
                .code("001")
                .build();

        BDDMockito.given(dynamoDB.getItem(Mockito.any(GetItemRequest.class)))
                .willReturn(cidResult);

        BDDMockito.given(cidResult.getItem())
                        .willReturn(null);

        BDDMockito.given(dynamoDB.putItem(
                Mockito.any(PutItemRequest.class)
        )).willThrow(conditionalCheckFailure);

        BDDMockito.given(conditionalCheckFailure.getErrorMessage())
                .willReturn("ConditionalCheckFailure");

        StepVerifier.create(sut.create(cid, product))
                .consumeErrorWith(thrown -> {
                    assertThat(thrown).isInstanceOf(ResourceAlreadyExistsException.class);
                    final var error = (ResourceAlreadyExistsException) thrown;

                    assertThat(error.getCode()).isEqualTo("PROD_002");
                    assertThat(error.getMessage()).isEqualTo("Code already exists");
                })
                .verify();
    }

    @Test
    void shouldHandleUnexpectedOperation() {
        final var cidResult = Mockito.mock(GetItemResult.class);
        final var cid = UUID.randomUUID().toString();
        final var product = Product.builder()
                .code("001")
                .build();

        BDDMockito.given(dynamoDB.getItem(Mockito.any(GetItemRequest.class)))
                .willReturn(cidResult);

        BDDMockito.given(cidResult.getItem()).willReturn(null);

        BDDMockito.given(dynamoDB.putItem(Mockito.any(PutItemRequest.class)))
                        .willThrow(UnsupportedOperationException.class);

       StepVerifier.create(sut.create(cid, product))
                .consumeErrorWith(thrown -> {
                    assertThat(thrown).isInstanceOf(UnexpectedOperationException.class);
                    final var error = (UnexpectedOperationException) thrown;

                    assertThat(error.getCode()).isEqualTo("APP_001");
                    assertThat(error.getMessage()).isEqualTo("We could not handle operation");
                })
                .verify();

    }

    @Test
    void shouldReturnMappedProduct() {
        final var cidResult = Mockito.mock(GetItemResult.class);
        final var putItemResult = Mockito.mock(PutItemResult.class);
        final var cid = UUID.randomUUID().toString();
        final var code = "001";
        final var product = Product.builder()
                .code(code)
                .build();

        final var expectedPutRequest = new PutItemRequest()
                .withTableName("Products")
                .withItem(Map.of("PK", new AttributeValue("PK")));

        BDDMockito.given(dynamoDB.getItem(Mockito.any(GetItemRequest.class)))
                .willReturn(cidResult);

        BDDMockito.given(cidResult.getItem()).willReturn(null);

        BDDMockito.given(dynamoDB.putItem(expectedPutRequest))
                .willReturn(putItemResult);

        BDDMockito.given(putItemResult.getAttributes())
                        .willReturn(Map.of(
                                "code", new AttributeValue("001")
                        ));

        StepVerifier.create(sut.create(cid, product))
                .consumeNextWith(p -> {
                    assertThat(p.getCode()).isEqualTo(code);
                })
                .verifyComplete();
    }


}