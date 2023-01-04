package com.dwx.ecommerce.products.adapter.output.persistence.dynamodb;

import com.amazonaws.services.dynamodbv2.document.ItemUtils;
import com.amazonaws.services.dynamodbv2.model.Put;
import com.dwx.ecommerce.products.adapter.output.persistence.core.DbConnection;
import com.dwx.ecommerce.products.adapter.output.persistence.dynamodb.core.DynamoOperationType;
import com.dwx.ecommerce.products.adapter.output.persistence.dynamodb.core.Transaction;
import com.dwx.ecommerce.products.adapter.output.persistence.dynamodb.core.command.DynamoWriteOperation;
import com.dwx.ecommerce.products.adapter.output.persistence.dynamodb.core.domain.PK;
import com.dwx.ecommerce.products.adapter.output.persistence.model.Product;
import reactor.core.publisher.Mono;

public class ProductCreateDynamoDBRepository extends Transaction<Product>
        implements ProductCreateRepository {
    public ProductCreateDynamoDBRepository(DbConnection connection) {
        super(connection);
    }

    @Override
    public Mono<Product> create(String cid, Product product) {

        return Mono.just(cid)
                .doOnNext(it -> {
                    final var pk =PK.builder()
                            .PK(product.getId())
                            .SK(product.getCode())
                            .build();

                    final var persist = new Put()
                            .withTableName("Products")
                            .withItem(ItemUtils.fromSimpleMap(pk.getId()));

                    this.add(DynamoWriteOperation.builder()
                            .type(DynamoOperationType.INSERT)
                            .identity(cid)
                            .operation(persist)
                            .build());
                })
                .map(it -> this.commit())
                .thenReturn(product)
                .doOnError(thrown -> {
                    System.out.println("We really need to handle this  problem");
                });



//     return   Mono.just(cid)
//                .flatMap(it -> Mono.defer(() -> {
//                    final var request  = new GetItemRequest()
//                            .withTableName("Products")
//                            .withKey(Map.of());
//
//                    final var idempotency =this.getConnection().getItem(request);
//
//                    if(idempotency.getItem() != null) {
//                        return Mono.error(new ResourceAlreadyExistsException("PROD_001", "Cid already in use"));
//                    }
//
//                    return Mono.just(cid);
//                }))
//                .map(it -> {
//
//                    final var productMapToPersist = Map.of(
//                            "PK", new AttributeValue("PK")
//                    );
//
//                    final var productRegisterRequest =new  PutItemRequest()
//                            .withTableName("Products")
//                            .withItem(productMapToPersist);
//
//                    final var productResult = dynamoDb.putItem(productRegisterRequest);
//
//                    return Product.builder()
//                            .code(productResult.getAttributes().get("code").getS())
//                            .build();
//                }).doOnError(thrown -> {
//                    if(thrown instanceof ConditionalCheckFailedException error) {
//                      throw new ResourceAlreadyExistsException("PROD_002", "Code already exists", error);
//                    }
//
//                    if(thrown instanceof ResourceAlreadyExistsException cidError) {
//                        throw new IdempotencyException("PROD_001", "Cid already in use");
//                    }
//
//                    throw new UnexpectedOperationException("APP_001", "We could not handle operation");
//                });
    }
}
