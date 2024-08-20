package com.dwx.ecommerce.products.adapter.output.persistence.dynamodb.core;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBAsync;
import com.amazonaws.services.dynamodbv2.document.ItemUtils;
import com.amazonaws.services.dynamodbv2.model.*;
import com.dwx.ecommerce.products.adapter.output.persistence.core.DbConnection;
import com.dwx.ecommerce.products.adapter.output.persistence.core.error.Error;
import com.dwx.ecommerce.products.adapter.output.persistence.core.error.ResourceNotFoundException;
import com.dwx.ecommerce.products.adapter.output.persistence.core.error.*;
import com.dwx.ecommerce.products.adapter.output.persistence.dynamodb.core.command.DynamoGetOperation;
import com.dwx.ecommerce.products.adapter.output.persistence.dynamodb.core.command.DynamoWriteOperation;
import com.dwx.ecommerce.products.adapter.output.persistence.dynamodb.core.domain.Model;
import com.dwx.ecommerce.products.adapter.output.persistence.dynamodb.core.domain.PK;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.*;

class TransactionUnitTest {
    ITransaction sut;
    DbConnection connection;
    AmazonDynamoDBAsync dynamoDBAsync;

    @BeforeEach
    void setUp() {
        connection = Mockito.mock(DbConnection.class);
        dynamoDBAsync = Mockito.mock(AmazonDynamoDBAsync.class);

        BDDMockito.given(connection.get())
                .willReturn(dynamoDBAsync);
    }

    @Nested
    class FindByIdTest {

        GetItemResult dynamoResult;

        @BeforeEach
        void setUp() {
            dynamoResult = Mockito.mock(GetItemResult.class);
            sut = new Transaction<ContextModelResult>(connection);
        }

        @Test
        void shouldReturnElementWhenItFindsIt() {
            Map resultMap = Map.of("code", "CODEX");
            final var id = PK.builder()
                    .PK("PK")
                    .SK("SK")
                    .build();

            BDDMockito.given(dynamoDBAsync.getItem(
                    Mockito.any(GetItemRequest.class)
            )).willReturn(dynamoResult);

            BDDMockito.given(dynamoResult.getItem())
                    .willReturn(ItemUtils.fromSimpleMap(resultMap));

            StepVerifier.create(
                            sut.findById(id, dbResult -> {
                                final var model = (Model) dbResult;
                                return new ContextModelResult(model);
                            })
                    ).consumeNextWith(next -> {
                        final var result = (ContextModelResult) next;
                        assertThat(result).isNotNull();
                        assertThat(result).isInstanceOf(ContextModelResult.class);
                    })
                    .verifyComplete();
        }

        @Test
        void shouldExecuteExternalOperation() {
            final var id = PK.builder()
                    .PK("PK")
                    .SK("SK")
                    .build();

            Map resultMap = Map.of("code", "CODEX");

            BDDMockito.given(dynamoDBAsync.getItem(
                    Mockito.any(GetItemRequest.class)
            )).willReturn(dynamoResult);

            BDDMockito.given(dynamoResult.getItem())
                    .willReturn(ItemUtils.fromSimpleMap(resultMap));

            StepVerifier.create(sut.findById(id, dbResult -> {
                        final var model = (Model) dbResult;
                        return new ContextModelResult(model);
                    }))
                    .consumeNextWith(result -> {
                        final var value = (ContextModelResult) result;
                        assertThat(value.getCode()).isEqualTo("CODEX");
                    })
                    .verifyComplete();
        }

        @Test
        void shouldThrowErrorWhenItemNotFound() {
            final var id = PK.builder()
                    .PK("PK")
                    .SK("SK")
                    .build();

            BDDMockito.given(dynamoDBAsync.getItem(
                    Mockito.any(GetItemRequest.class)
            )).willReturn(dynamoResult);

            BDDMockito.given(dynamoResult.getItem())
                    .willReturn(null);

            StepVerifier.create(sut.findById(id, dbModel -> new ContextModelResult(null)))
                    .consumeErrorWith(thrown -> {
                        assertThat(thrown)
                                .isInstanceOf(ResourceNotFoundException.class)
                                .hasMessageContaining("Item not found")
                                .hasFieldOrPropertyWithValue("code", "EDB002");

                    })
                    .verify();
        }

        @Test
        void shouldHandleTableRestrictionNonexistenceError() {
            final var id = PK.builder()
                    .PK("PK")
                    .SK("SK")
                    .build();

            BDDMockito.given(dynamoDBAsync.getItem(
                    Mockito.any(GetItemRequest.class)
            )).willThrow(new com.amazonaws.services.dynamodbv2.model.ResourceNotFoundException("error"));

            StepVerifier.create(sut.findById(id, dbModel -> new ContextModelResult(null)))
                    .consumeErrorWith(thrown -> {
                        assertThat(thrown)
                                .hasCauseExactlyInstanceOf(com.amazonaws.services.dynamodbv2.model.ResourceNotFoundException.class)
                                .hasMessageContaining("Table not found")
                                .hasFieldOrPropertyWithValue("code", "EDB003");

                    })
                    .verify();
        }

        @Test
        void shouldHandleDBProviderUntraceableErrors() {
            sut = new Transaction<ContextModelResult>(connection);
            final var id = PK.builder()
                    .PK("PK")
                    .SK("SK")
                    .build();

            BDDMockito.given(dynamoDBAsync.getItem(
                    Mockito.any(GetItemRequest.class)
            )).willThrow(new InternalServerErrorException("drop aws"));

            StepVerifier.create(sut.findById(id, dbModel -> new ContextModelResult(null)))
                    .consumeErrorWith(thrown -> {
                        assertThat(thrown)
                                .isInstanceOf(UnexpectedProviderBehaviorException.class)
                                .hasCauseExactlyInstanceOf(com.amazonaws.services.dynamodbv2.model.InternalServerErrorException.class)
                                .hasFieldOrPropertyWithValue("code", "EDB001")
                                .hasMessageContaining("Unexpected error");

                    })
                    .verify();
        }


        static class ContextModelResult {
            private String code;

            public ContextModelResult(Model model) {
                final var item = model.getItem();
                this.code = item.get("code").toString();
            }

            public String getCode() {
                return this.code;
            }
        }
    }

    @Nested
    class AddTest {

        @BeforeEach
        void setUp() {
            sut = new Transaction(connection);
        }

        @Test
        void shouldThrowErrorWhenNonIdentityCheck() {
            final var writeOperation = DynamoWriteOperation
                    .builder()
                    .build();
            assertThatThrownBy(() -> sut.add(writeOperation))
                    .isInstanceOf(NonIdentifiableTransactionException.class)
                    .hasMessageContaining("You must specify the operation identifier")
                    .hasFieldOrPropertyWithValue("code", "EDB004");
        }

        @Test
        void shouldThrowErrorWhenOperationsHaveTheSameIdentifier() {
            final var writeOperation = DynamoWriteOperation
                    .builder()
                    .identity("id")
                    .build();

            final var writeOperation1 = DynamoWriteOperation
                    .builder()
                    .identity("id2")
                    .build();


            assertThatThrownBy(() -> {
                sut.add(writeOperation);
                sut.add(writeOperation1);
            })
                    .isInstanceOf(NonUniqueIdentifierException.class)
                    .hasMessageContaining("Operations must have same identity")
                    .hasFieldOrPropertyWithValue("code", "EDB005");
        }

        @Test
        void shouldThrowErrorWhenOperationLimitOverpass() {
            assertThatThrownBy(() -> {
                IntStream.range(0, 35)
                        .forEach(index -> {
                            final var writeOperation = DynamoWriteOperation
                                    .builder()
                                    .identity("id")
                                    .build();

                            sut.add(writeOperation);
                        });
            })
                    .isInstanceOf(OperationLimitOverpassException.class)
                    .hasMessageContaining("Operations must have at most 30 items")
                    .hasFieldOrPropertyWithValue("code", "EDB006");
        }

        @Test
        void shouldThrowErrorWhenNonMatchOperation() {
            final var writeOperation = DynamoWriteOperation
                    .builder()
                    .identity("id")
                    .build();

            final var getOperation = DynamoGetOperation
                    .builder()
                    .identity("id2")
                    .build();

            sut.add(writeOperation);
            assertThatThrownBy(() -> sut.add(getOperation))
                    .hasMessageContaining("Operations must have same identity");
        }
    }

    @Nested
    class CommitTest {

        @BeforeEach
        void setUp() {
            sut = new Transaction(connection);
        }

        @Test
        void shouldThrowErrorWhenNoTransactionAdded() throws ExecutionException, InterruptedException {
            assertThatThrownBy(() -> sut.commit())
                    .isInstanceOf(NoTransactionOperationDefinedException.class)
                    .hasMessageContaining("No transaction defined")
                    .hasFieldOrPropertyWithValue("code", "EDB007");
        }

        @Test
        void shouldCommitSuccessfully() {
            final var futureTransactionResult = Mockito.mock(Future.class);
            final var putOperation = Mockito.mock(Put.class);
            final var writeOperation = DynamoWriteOperation
                    .builder()
                    .identity("id")
                    .operation(putOperation)
                    .build();

            final var transaction = new TransactWriteItemsRequest()
                    .withTransactItems(new TransactWriteItem().withPut(putOperation));

            BDDMockito.given(dynamoDBAsync.transactWriteItemsAsync(transaction))
                    .willReturn(futureTransactionResult);

            sut.add(writeOperation);

            StepVerifier.create(sut.commit())
                    .consumeNextWith(hasCommited -> assertThat(hasCommited).isEqualTo(Boolean.TRUE))
                    .verifyComplete();
        }

        @Test
        void shouldCommitCallOperationProvider() throws ExecutionException, InterruptedException {
            final var futureTransactionResult = Mockito.mock(Future.class);
            final var result = Mockito.mock(TransactWriteItemsResult.class);
            final var putOperation = Mockito.mock(Put.class);
            final var writeOperation = DynamoWriteOperation
                    .builder()
                    .identity("id")
                    .operation(putOperation)
                    .build();
            final var operation = new TransactWriteItem()
                    .withPut(putOperation);

            final var transaction = new TransactWriteItemsRequest()
                    .withTransactItems(List.of(operation));

            BDDMockito.given(dynamoDBAsync.transactWriteItemsAsync(Mockito.any(TransactWriteItemsRequest.class)))
                    .willReturn(futureTransactionResult);

            BDDMockito.given(futureTransactionResult.get())
                    .willReturn(result);
            sut.add(writeOperation);

            StepVerifier.create(sut.commit())
                    .consumeNextWith(hasCommitted -> {
                        assertThat(hasCommitted).isEqualTo(Boolean.TRUE);
                        Mockito.verify(dynamoDBAsync).transactWriteItemsAsync(transaction);
                    })
                    .verifyComplete();
        }

        @Test
        void shouldThrowErrorCommitOperationFail() throws ExecutionException, InterruptedException {
            final var mockError = Mockito.mock(TransactionCanceledException.class);
            final var putOperation = Mockito.mock(Put.class);
            final var writeOperation = DynamoWriteOperation
                    .builder()
                    .identity("id")
                    .errorConverter(ce -> new ResourceNotFoundException("20", "NotFound"))
                    .operation(putOperation)
                    .build();

            BDDMockito.given(dynamoDBAsync.transactWriteItemsAsync(Mockito.any(TransactWriteItemsRequest.class)))
                    .willThrow(mockError);

            sut.add(writeOperation);

            StepVerifier.create(sut.commit())
                    .consumeErrorWith(thrown -> {
                        assertThat(thrown).isNotNull();
                        assertThat(thrown).isNotInstanceOf(TransactionCanceledException.class);
                    })
                    .verify();
        }

        @Test
        void shouldThrowErrorForSpecificTransactionItem() throws ExecutionException, InterruptedException {
            final var executionError = Mockito.mock(ExecutionException.class);
            final var mockError = Mockito.mock(TransactionCanceledException.class);
            final var mockReason1 = Mockito.mock(CancellationReason.class);
            final var mockReason2 = Mockito.mock(CancellationReason.class);
            final var putOperation = Mockito.mock(Put.class);
            final var futureResult = Mockito.mock(Future.class);
            final var writeOperation1 = DynamoWriteOperation
                    .builder()
                    .identity("id")
                    .errorConverter(ce -> new ResourceNotFoundException("20", "NotFound"))
                    .operation(putOperation)
                    .build();

            final var writeOperation2 = DynamoWriteOperation
                    .builder()
                    .identity("id")
                    .errorConverter(ce -> new ResourceNotFoundException("21", "NotFound1"))
                    .operation(putOperation)
                    .build();

            BDDMockito.given(dynamoDBAsync.transactWriteItemsAsync(Mockito.any(TransactWriteItemsRequest.class)))
                    .willReturn(futureResult);

            BDDMockito.given(futureResult.get())
                    .willThrow(executionError);

            BDDMockito.given(executionError.getCause())
                    .willReturn(mockError);

            BDDMockito.given(mockError.getCancellationReasons())
                    .willReturn(List.of(mockReason1, mockReason2));

            BDDMockito.given(mockReason1.getCode())
                    .willReturn("SOMETHING_ELSE");

            BDDMockito.given(mockReason2.getCode())
                    .willReturn("ConditionalCheckFailed");

            sut.add(writeOperation1);
            sut.add(writeOperation2);

            StepVerifier.create(sut.commit())
                    .consumeErrorWith(thrown -> {
                        assertThat(thrown).isNotNull();
                        assertThat(thrown).isNotInstanceOf(CancellationReason.class);

                        final var error = (ResourceNotFoundException) thrown;

                        assertThat(error.getCode()).isEqualTo("21");
                        assertThat(error.getMessage()).isEqualTo("NotFound1");
                    })
                    .verify();
        }

        @Test
        void shouldThrowUnexpectedErrorWhenNoConditionalCheckErrorHaveThrown() throws ExecutionException, InterruptedException {
            final var futureError = Mockito.mock(ExecutionException.class);
            final var errorResult = Mockito.mock(TransactionCanceledException.class);
            final var mockReason1 = Mockito.mock(CancellationReason.class);
            final var putOperation = Mockito.mock(Put.class);
            final var futureResult = Mockito.mock(Future.class);
            final var writeOperation1 = DynamoWriteOperation
                    .builder()
                    .identity("id")
                    .errorConverter(ce -> new ResourceNotFoundException("20", "NotFound"))
                    .operation(putOperation)
                    .build();

            BDDMockito.given(dynamoDBAsync.transactWriteItemsAsync(Mockito.any(TransactWriteItemsRequest.class)))
                    .willReturn(futureResult);

            BDDMockito.given(futureResult.get())
                    .willThrow(futureError);

            BDDMockito.given(futureError.getCause())
                    .willReturn(errorResult);

            BDDMockito.given(errorResult.getCancellationReasons())
                    .willReturn(List.of(mockReason1));

            BDDMockito.given(mockReason1.getCode())
                    .willReturn("THROTTLING");

            sut.add(writeOperation1);

            StepVerifier.create(sut.commit())
                    .consumeErrorWith(thrown -> {
                        assertThat(thrown).isNotNull();
                        assertThat(thrown).isInstanceOf(UnexpectedProviderBehaviorException.class);

                        final var error = (UnexpectedProviderBehaviorException) thrown;

                        assertThat(error.getCode()).isEqualTo(Error.UNEXPECTED_BEHAVIOR.getCode());
                        assertThat(error.getMessage()).isEqualTo("Provider error");
                        assertThat(error.getCause()).isInstanceOf(TransactionCanceledException.class);
                    })
                    .verify();
        }
    }
}