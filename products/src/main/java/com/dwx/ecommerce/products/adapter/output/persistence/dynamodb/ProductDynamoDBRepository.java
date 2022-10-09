package com.dwx.ecommerce.products.adapter.output.persistence.dynamodb;

import com.dwx.ecommerce.products.adapter.output.persistence.model.Product;
import reactor.core.publisher.Mono;

public class ProductDynamoDBRepository implements ProductRepository {
    @Override
    public Mono<Product> add(String cid, Product product) {
        return Mono.just(cid)
                .flatMap(it -> Mono.error(new CidAlreadyInUseException()));
    }
}
