package com.dwx.ecommerce.products.adapter.output.persistence.dynamodb.core;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBAsync;
import com.amazonaws.services.dynamodbv2.document.ItemUtils;
import com.amazonaws.services.dynamodbv2.model.*;
import com.dwx.ecommerce.products.adapter.output.persistence.core.DbConnection;
import com.dwx.ecommerce.products.adapter.output.persistence.core.command.Operation;
import com.dwx.ecommerce.products.adapter.output.persistence.core.error.Error;
import com.dwx.ecommerce.products.adapter.output.persistence.core.error.ResourceNotFoundException;
import com.dwx.ecommerce.products.adapter.output.persistence.core.error.TableNotFoundException;
import com.dwx.ecommerce.products.adapter.output.persistence.core.error.UnsupportedOperationException;
import com.dwx.ecommerce.products.adapter.output.persistence.core.error.*;
import com.dwx.ecommerce.products.adapter.output.persistence.dynamodb.core.domain.DynamoModel;
import com.dwx.ecommerce.products.adapter.output.persistence.dynamodb.core.domain.Model;
import com.dwx.ecommerce.products.adapter.output.persistence.dynamodb.core.domain.PK;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class Transaction<T> implements ITransaction<T> {
    private final static String ERROR_MESSAGE_TABLE_NOT_FOUND = "Table not found";
    private final static String ERROR_MESSAGE_ITEM_NOT_FOUND = "Item not found";
    private final static String ERROR_MESSAGE_UNEXPECTED_ERROR = "Unexpected error";

    private final static String CONDITIONAL_CHECK_FAILED = "ConditionalCheckFailed";
    private final static int TRANSACTION_LIMIT = 30;

    @Getter
    private final DbConnection connection;
    private final List<Operation> operations = new ArrayList<>();

    private final Function<Throwable, UnexpectedProviderBehaviorException> handleAwsError = error -> new UnexpectedProviderBehaviorException(
            Error.UNEXPECTED_BEHAVIOR.getCode(),
            "Provider error",
            error
    );


    @Override
    public Mono<T> findById(PK id, Function<Model, T> mapper) {
        return Mono.just(id)
                .map(pk -> new GetItemRequest().withKey(ItemUtils.fromSimpleMap(id.getId())))
                .map(request -> ((AmazonDynamoDBAsync) connection.get()).getItem(request))
                .map(this::mapDynamoResponse)
                .map(DynamoModel::fromSource)
                .map(mapper)
                .onErrorMap(this::errorHandler);
    }

    @Override
    public void add(Operation operation) {
        validateIdentity(operation);
        validateLimit();
        validateUniqueID(operation);
        this.operations.add(operation);
    }

    @Override
    public Mono<Boolean> commit() {
        if (this.operations.isEmpty()) {
            throw new NoTransactionOperationDefinedException(
                    Error.NO_TRANSACTION_DEFINED.getCode(),
                    "No transaction defined"
            );
        }
        final var dynamo = (AmazonDynamoDBAsync) connection.get();

        final var type = (DynamoOperationType) operations.get(0).getType();


        final var dynamoOperations = operations.stream()
                .map(o -> (TransactWriteItem) DynamoOperationType.build(type.getName()).apply(o))
                .collect(Collectors.toList());

        if (type.equals(DynamoOperationType.INSERT) || type.equals(DynamoOperationType.UPDATE)) {
            final var transaction = new TransactWriteItemsRequest()
                    .withTransactItems(dynamoOperations);

            return Mono.fromFuture(() -> CompletableFuture.supplyAsync(
                            () -> {
                                try {
                                    return dynamo.transactWriteItemsAsync(transaction).get();
                                } catch (InterruptedException e) {
                                    throw new RuntimeException(e);
                                } catch (ExecutionException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                    ))
                    .onErrorMap(thrown -> {
                        final var transactionError = thrown.getCause().getCause();
                        if (transactionError instanceof TransactionCanceledException error) {
                            return error.getCancellationReasons()
                                    .stream()
                                    .filter(reason -> reason.getCode().equals(CONDITIONAL_CHECK_FAILED))
                                    .findFirst()
                                    .map(e -> {
                                        final var errorIndexMatcher = error.getCancellationReasons().indexOf(e);
                                        final var operationError = operations.get(errorIndexMatcher)
                                                .getErrorConverter()
                                                .apply(e);

                                        return (Throwable) operationError;
                                    })
                                    .orElseThrow(() ->handleAwsError.apply(error));
                        }

                        return handleAwsError.apply(thrown);
                    })
                    .doOnSuccess(ignored -> operations.clear())
                    .thenReturn(Boolean.TRUE);

        }

        throw  new UnsupportedOperationException();
    }


    private Map<String, AttributeValue> mapDynamoResponse(GetItemResult response) {
        final var hasItemFound = response != null
                && response.getItem() != null
                && response.getItem().size() > 0;

        if (!hasItemFound) {
            throw new ResourceNotFoundException(
                    Error.RESOURCE_NOT_FOUND.getCode(),
                    ERROR_MESSAGE_ITEM_NOT_FOUND
            );
        }

        return response.getItem();
    }

    private Throwable errorHandler(Throwable thrown) {
        if (thrown instanceof ResourceNotFoundException) {
            return thrown;
        }

        if (thrown instanceof com.amazonaws.services.dynamodbv2.model.ResourceNotFoundException) {
            return new TableNotFoundException(
                    Error.TABLE_NOT_EXISTS.getCode(),
                    ERROR_MESSAGE_TABLE_NOT_FOUND,
                    thrown
            );
        }

        return new UnexpectedProviderBehaviorException(
                Error.UNEXPECTED_BEHAVIOR.getCode(),
                ERROR_MESSAGE_UNEXPECTED_ERROR,
                thrown
        );
    }

    private void validateIdentity(Operation operation) {
        final var hasIdentity = operation.getIdentity() != null;

        if (!hasIdentity) {
            throw new NonIdentifiableTransactionException(
                    Error.NON_INDENTIFIABLE_TRANSACTION.getCode(),
                    "You must specify the operation identifier"
            );
        }
    }

    private void validateLimit() {
        final var hasOverpassLimit = this.operations.size() > TRANSACTION_LIMIT;

        if (hasOverpassLimit) {
            throw new OperationLimitOverpassException(
                    Error.LIMIT_TRANSACTION_OVERPASS.getCode(),
                    "Operations must have at most 30 items"
            );

        }
    }

    private void validateUniqueID(Operation operation) {
        final var hasDifferentId = this.operations.stream()
                .anyMatch(opr -> opr.getIdentity().compareTo(operation.getIdentity()) != 0);

        if (hasDifferentId) {
            throw new NonUniqueIdentifierException(
                    Error.NON_UNIQUE_TRANSACTION.getCode(),
                    "Operations must have same identity"
            );
        }

    }


}
