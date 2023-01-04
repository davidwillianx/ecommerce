package com.dwx.ecommerce.products.adapter.output.persistence.dynamodb.core;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBAsync;
import com.amazonaws.services.dynamodbv2.document.ItemUtils;
import com.amazonaws.services.dynamodbv2.model.*;
import com.dwx.ecommerce.products.adapter.output.persistence.core.DbConnection;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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
        void shouldHandleDBProviderUntrackeableErrors() {
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
        void shouldCommitCallOperationProvider() {
            final var futureTransactionResult = Mockito.mock(Future.class);
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

            sut.add(writeOperation);

            StepVerifier.create(sut.commit())
                    .consumeNextWith(hasCommited -> {
                        assertThat(hasCommited).isEqualTo(Boolean.TRUE);
                        Mockito.verify(dynamoDBAsync).transactWriteItemsAsync(transaction);
                    })
                    .verifyComplete();
        }


    }
}