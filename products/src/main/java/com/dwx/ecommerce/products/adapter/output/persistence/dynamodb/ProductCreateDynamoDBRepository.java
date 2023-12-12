package com.dwx.ecommerce.products.adapter.output.persistence.dynamodb;

import com.amazonaws.services.dynamodbv2.document.ItemUtils;
import com.amazonaws.services.dynamodbv2.model.Put;
import com.dwx.ecommerce.products.adapter.output.persistence.core.DbConnection;
import com.dwx.ecommerce.products.adapter.output.persistence.dynamodb.core.IdempotencyOperationBuilder;
import com.dwx.ecommerce.products.adapter.output.persistence.dynamodb.core.Transaction;
import com.dwx.ecommerce.products.adapter.output.persistence.dynamodb.core.command.DynamoWriteOperation;
import com.dwx.ecommerce.products.adapter.output.persistence.dynamodb.core.domain.PK;
import com.dwx.ecommerce.products.adapter.output.persistence.error.ResourceAlreadyExistsException;
import com.dwx.ecommerce.products.adapter.output.persistence.model.ProductDto;
import com.dwx.ecommerce.products.application.domain.Product;
import com.dwx.ecommerce.products.application.ports.database.ProductCreateRepository;
import reactor.core.publisher.Mono;


public class ProductCreateDynamoDBRepository extends Transaction<ProductDto>
        implements ProductCreateRepository {

    private static final String ENTITY_NAME = "Products";

    public ProductCreateDynamoDBRepository(DbConnection connection) {
        super(connection);
    }

    @Override
    public Mono<Product> execute(String trackingId, Product product) {

        return Mono.just(trackingId)
                .doOnNext(it -> {
                    final var pk = PK.builder()
                            .PK(product.getCode())
                            .SK(product.getCategory().name())
                            .build();

                    final var persist = new Put()
                            .withTableName(ENTITY_NAME)
                            .withItem(ItemUtils.fromSimpleMap(pk.getId()));

                    this.add(IdempotencyOperationBuilder.builder()
                            .transactionId(trackingId)
                            .entityName(ENTITY_NAME)
                            .pk(pk)
                            .build()
                            .getOperation()
                    );

                    this.add(DynamoWriteOperation.builder()
                            .identity(trackingId)
                            .operation(persist)
                            .errorConverter(e -> new ResourceAlreadyExistsException(
                                    "Code already exists"
                            ))
                            .build());
                })
                .flatMap(it -> Mono.defer(this::commit))
                .map(succeed -> product);
    }
}
