package com.dwx.ecommerce.products.application.ports.database;

import com.dwx.ecommerce.products.application.domain.Product;
import reactor.core.publisher.Mono;

public interface ProductCreateRepository {
    Mono<Product> execute(String trackingId, Product product);
}
