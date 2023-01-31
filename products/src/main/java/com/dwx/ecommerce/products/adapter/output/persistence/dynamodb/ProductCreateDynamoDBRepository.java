package com.dwx.ecommerce.products.adapter.output.persistence.dynamodb;

import com.amazonaws.services.dynamodbv2.document.ItemUtils;
import com.amazonaws.services.dynamodbv2.model.Put;
import com.dwx.ecommerce.products.adapter.output.persistence.core.DbConnection;
import com.dwx.ecommerce.products.adapter.output.persistence.dynamodb.core.IdempotencyOperationBuilder;
import com.dwx.ecommerce.products.adapter.output.persistence.dynamodb.core.Transaction;
import com.dwx.ecommerce.products.adapter.output.persistence.dynamodb.core.command.DynamoWriteOperation;
import com.dwx.ecommerce.products.adapter.output.persistence.dynamodb.core.domain.PK;
import com.dwx.ecommerce.products.adapter.output.persistence.error.ResourceAlreadyExistsException;
import com.dwx.ecommerce.products.adapter.output.persistence.model.Product;
import reactor.core.publisher.Mono;


public class ProductCreateDynamoDBRepository extends Transaction<Product>
        implements ProductCreateRepository {

    private static final String ENTITY_NAME = "Products";

    public ProductCreateDynamoDBRepository(DbConnection connection) {
        super(connection);
    }

    @Override
    public Mono<Product> create(String cid, Product product) {

        return Mono.just(cid)
                .doOnNext(it -> {
                    final var pk = PK.builder()
                            .PK(product.getId())
                            .SK(product.getCode())
                            .build();

                    final var persist = new Put()
                            .withTableName(ENTITY_NAME)
                            .withItem(ItemUtils.fromSimpleMap(pk.getId()));

                    this.add(IdempotencyOperationBuilder.builder()
                            .transactionId(cid)
                            .entityName(ENTITY_NAME)
                            .pk(pk)
                            .build()
                            .getOperation()
                    );

                    this.add(DynamoWriteOperation.builder()
                            .identity(cid)
                            .operation(persist)
                            .errorConverter(e -> new ResourceAlreadyExistsException(
                                    "Code already exists"
                            ))
                            .build());
                })
                .flatMap(it -> Mono.defer(this::commit))
                .thenReturn(product);
    }
}
