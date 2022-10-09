package com.dwx.ecommerce.products.adapter.output.persistence.dynamodb;

import com.dwx.ecommerce.products.adapter.output.persistence.model.Product;
import reactor.core.publisher.Mono;

public interface ProductRepository {
  Mono<Product> add(String cid, Product product);
}
