package com.dwx.ecommerce.products.application.ports.database;

import com.dwx.ecommerce.products.application.domain.PageAttributes;
import com.dwx.ecommerce.products.application.domain.Product;
import reactor.core.publisher.Flux;

public interface ProductMultipleRetrievalRepository {
    Flux<Product> execute(String cid, PageAttributes attributes);
}
