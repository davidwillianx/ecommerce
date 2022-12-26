package com.dwx.ecommerce.products.adapter.output.persistence.dynamodb;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBAsync;
import com.amazonaws.services.dynamodbv2.model.*;
import com.dwx.ecommerce.products.adapter.output.persistence.error.IdempotencyException;
import com.dwx.ecommerce.products.adapter.output.persistence.error.ResourceAlreadyExistsException;
import com.dwx.ecommerce.products.adapter.output.persistence.error.UnexpectedOperationException;
import com.dwx.ecommerce.products.adapter.output.persistence.model.Product;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.Map;

@RequiredArgsConstructor
public class ProductCreateDynamoDBRepository implements ProductCreateRepository {

   private final AmazonDynamoDBAsync dynamoDb;
    @Override
    public Mono<Product> create(String cid, Product product) {
     return   Mono.just(cid)
                .flatMap(it -> Mono.defer(() -> {
                    final var request  = new GetItemRequest()
                            .withTableName("Products")
                            .withKey(Map.of());

                    final var idempotency = dynamoDb.getItem(request);

                    if(idempotency.getItem() != null) {
                        return Mono.error(new ResourceAlreadyExistsException("PROD_001", "Cid already in use"));
                    }

                    return Mono.just(cid);
                }))
                .map(it -> {

                    final var productMapToPersist = Map.of(
                            "PK", new AttributeValue("PK")
                    );

                    final var productRegisterRequest =new  PutItemRequest()
                            .withTableName("Products")
                            .withItem(productMapToPersist);

                    final var productResult = dynamoDb.putItem(productRegisterRequest);

                    return Product.builder()
                            .code(productResult.getAttributes().get("code").getS())
                            .build();
                }).doOnError(thrown -> {
                    if(thrown instanceof ConditionalCheckFailedException error) {
                      throw new ResourceAlreadyExistsException("PROD_002", "Code already exists", error);
                    }

                    if(thrown instanceof ResourceAlreadyExistsException cidError) {
                        throw new IdempotencyException("PROD_001", "Cid already in use");
                    }

                    throw new UnexpectedOperationException("APP_001", "We could not handle operation");
                });
    }
}
