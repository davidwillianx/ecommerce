package com.dwx.ecommerce.products.adapter.output.dynamodb;

import reactor.core.publisher.Mono;

public interface ProductRepository {
  Mono<Product> add(String cid, Product product);
}
