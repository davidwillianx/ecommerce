package com.dwx.ecommerce.products.adapter.output.persistence.dynamodb;

import com.dwx.ecommerce.products.adapter.output.persistence.model.ProductDto;
import reactor.core.publisher.Mono;

public interface ProductRepository {
  Mono<ProductDto> add(String cid, ProductDto product);
}
