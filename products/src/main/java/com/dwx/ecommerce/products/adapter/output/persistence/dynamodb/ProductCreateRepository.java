package com.dwx.ecommerce.products.adapter.output.persistence.dynamodb;

import com.dwx.ecommerce.products.adapter.output.persistence.model.Product;
import reactor.core.publisher.Mono;

public interface ProductCreateRepository {
    Mono<Product> create(String cid, Product product);
}
