package com.dwx.ecommerce.products.adapter.output.persistece.dynamodb;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBAsync;
import com.amazonaws.services.dynamodbv2.model.CancellationReason;
import com.amazonaws.services.dynamodbv2.model.TransactWriteItemsResult;
import com.amazonaws.services.dynamodbv2.model.TransactionCanceledException;
import com.dwx.ecommerce.products.adapter.output.persistence.core.DbConnection;
import com.dwx.ecommerce.products.adapter.output.persistence.core.error.UnexpectedProviderBehaviorException;
import com.dwx.ecommerce.products.adapter.output.persistence.dynamodb.ProductCreateDynamoDBRepository;
import com.dwx.ecommerce.products.application.domain.Product;
import com.dwx.ecommerce.products.application.ports.database.ProductCreateRepository;
import com.dwx.ecommerce.products.adapter.output.persistence.error.IdempotencyException;
import com.dwx.ecommerce.products.adapter.output.persistence.error.ResourceAlreadyExistsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static org.assertj.core.api.Assertions.assertThat;

class ProductCreateDynamoDBRepositoryUnitTest {

    ProductCreateRepository sut;
    DbConnection connection;
    AmazonDynamoDBAsync dynamoDBAsync;

    @BeforeEach
    void setUp() {
        connection = Mockito.mock(DbConnection.class);
        dynamoDBAsync = Mockito.mock(AmazonDynamoDBAsync.class);

        BDDMockito.given(connection.get())
                .willReturn(dynamoDBAsync);

        sut = new ProductCreateDynamoDBRepository(connection);
    }

    @Test
    void shouldThrowErrorWhenIdempotencyKeyIsAlreadyInUse() throws ExecutionException, InterruptedException {
        final var error = Mockito.mock(ExecutionException.class);
        final var transactCanceledException = Mockito.mock(TransactionCanceledException.class);
        final var futureResult = Mockito.mock(Future.class);

        final var cid = UUID.randomUUID().toString();

        final var product = Product.builder()
                .id(UUID.randomUUID().toString())
                .code(UUID.randomUUID().toString())
                .build();
        final var reasons = List.of(
                new CancellationReason().withCode("ConditionalCheckFailed")
        );

        BDDMockito.given(dynamoDBAsync.transactWriteItemsAsync(Mockito.any()))
                .willReturn(futureResult);

        BDDMockito.given(futureResult.get())
                .willThrow(error);

        BDDMockito.given(error.getCause())
                .willReturn(transactCanceledException);

        BDDMockito.given(transactCanceledException.getCancellationReasons())
                .willReturn(reasons);

        StepVerifier.create(sut.execute(cid, product))
                .consumeErrorWith(thrown -> {
                    assertThat(thrown).isInstanceOf(IdempotencyException.class);
                    final var result = (IdempotencyException) thrown;

                    assertThat(result.getCode()).isEqualTo("EDB005");
                    assertThat(result.getMessage()).isEqualTo("Idempotency check already in use");
                })
                .verify();
    }

    @Test
    void shouldThrowErrorCodeAlreadyExists() throws ExecutionException, InterruptedException {
        final var error = Mockito.mock(ExecutionException.class);
        final var transactCanceledException = Mockito.mock(TransactionCanceledException.class);
        final var cid = UUID.randomUUID().toString();
        final var futureResult = Mockito.mock(Future.class);
        final var productId = UUID.randomUUID().toString();
        final var code = UUID.randomUUID().toString();
        final var product = Product.builder()
                .id(productId)
                .code(code)
                .build();

        final var reasons = List.of(
                new CancellationReason().withCode("NOTHING_ELSE_MATTER"),
                new CancellationReason().withCode("ConditionalCheckFailed")
        );


        BDDMockito.given(dynamoDBAsync.transactWriteItemsAsync(Mockito.any()))
                .willReturn(futureResult);

        BDDMockito.given(futureResult.get())
                .willThrow(error);

        BDDMockito.given(error.getCause())
                .willReturn(transactCanceledException);

        BDDMockito.given(transactCanceledException.getCancellationReasons())
                .willReturn(reasons);

        StepVerifier.create(sut.execute(cid, product))
                .consumeErrorWith(thrown -> {
                    assertThat(thrown).isInstanceOf(ResourceAlreadyExistsException.class);
                    final var result = (ResourceAlreadyExistsException) thrown;

                    assertThat(result.getCode()).isEqualTo("EDB008");
                    assertThat(result.getMessage()).isEqualTo("Code already exists");
                })
                .verify();
    }

    @Test
    void shouldHandleUnexpectedOperation() throws ExecutionException, InterruptedException {
        final var cid = UUID.randomUUID().toString();
        final var futureResult = Mockito.mock(Future.class);
        final var product = Product.builder()
                .id(UUID.randomUUID().toString())
                .code("001")
                .build();

        BDDMockito.given(dynamoDBAsync.transactWriteItemsAsync(Mockito.any()))
                .willReturn(futureResult);

        BDDMockito.given(futureResult.get())
                .willThrow(new InterruptedException());


       StepVerifier.create(sut.execute(cid, product))
                .consumeErrorWith(thrown -> {
                    assertThat(thrown).isInstanceOf(UnexpectedProviderBehaviorException.class);
                    final var error = (UnexpectedProviderBehaviorException) thrown;

                    assertThat(error.getCode()).isEqualTo("EDB001");
                    assertThat(error.getMessage()).isEqualTo("Provider error");
                })
                .verify();

    }

    @Test
    void shouldReturnMappedProduct() throws ExecutionException, InterruptedException {
        final var cid = UUID.randomUUID().toString();
        final var futureResult = Mockito.mock(Future.class);
        final var transactionResult = Mockito.mock(TransactWriteItemsResult.class);
        final var code = "001";
        final var product = Product.builder()
                .id(UUID.randomUUID().toString())
                .code(code)
                .build();

        BDDMockito.given(dynamoDBAsync.transactWriteItemsAsync(Mockito.any()))
                .willReturn(futureResult);

        BDDMockito.given(futureResult.get())
                        .willReturn(transactionResult);

        StepVerifier.create(sut.execute(cid, product))
                .consumeNextWith(p ->
                    assertThat(p.getCode()).isEqualTo(code)
                )
                .verifyComplete();
    }


}