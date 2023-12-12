package com.dwx.ecommerce.products.application.usecase;

import com.dwx.ecommerce.products.application.domain.Product;
import reactor.core.publisher.Mono;

public interface CreateProductUseCase {
    Mono<Product> execute(String trackingId, Product product);
}
