package com.dwx.ecommerce.products.adapter.output.persistence.dynamodb;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBAsync;
import com.amazonaws.services.dynamodbv2.document.ItemUtils;
import com.amazonaws.services.dynamodbv2.model.Put;
import com.dwx.ecommerce.products.adapter.output.persistence.core.DbConnection;
import com.dwx.ecommerce.products.adapter.output.persistence.dynamodb.core.IdempotencyOperationBuilder;
import com.dwx.ecommerce.products.adapter.output.persistence.dynamodb.core.Transaction;
import com.dwx.ecommerce.products.adapter.output.persistence.dynamodb.core.command.DynamoWriteOperation;
import com.dwx.ecommerce.products.adapter.output.persistence.dynamodb.core.domain.PK;
import com.dwx.ecommerce.products.adapter.output.persistence.error.ResourceAlreadyExistsException;
import com.dwx.ecommerce.products.application.domain.Product;
import com.dwx.ecommerce.products.application.ports.database.ProductCreateRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;


@RequiredArgsConstructor
public class ProductCreateDynamoDBRepository implements ProductCreateRepository {

    private static final String ENTITY_NAME = "Products";
    private final DbConnection<AmazonDynamoDBAsync> connection;


    @Override
    public Mono<Product> execute(String trackingId, Product product) {
        final var transaction = new Transaction<>(connection);
        return Mono.just(transaction)
                .map(it -> {
                    final var pk = PK.builder()
                            .PK(product.getCode())
                            .SK(product.getCategory().name())
                            .build();

                    final var persist = new Put()
                            .withTableName(ENTITY_NAME)
                            .withItem(ItemUtils.fromSimpleMap(pk.getId()));

                    transaction.add(IdempotencyOperationBuilder.builder()
                            .transactionId(trackingId)
                            .entityName(ENTITY_NAME)
                            .pk(pk)
                            .build()
                            .getOperation()
                    );

                    transaction.add(DynamoWriteOperation.builder()
                            .identity(trackingId)
                            .operation(persist)
                            .errorConverter(e -> new ResourceAlreadyExistsException(
                                    "Code already exists"
                            ))
                            .build()
                    );

                    return it;
                })
                .flatMap(Transaction::commit)
                .map(succeed -> product);
    }
}
