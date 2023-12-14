package com.dwx.ecommerce.products.application.ports.database;

import com.dwx.ecommerce.products.application.domain.PageAttributes;
import reactor.core.publisher.Flux;

public interface ProductMultipleRetrievalRepository {
    Flux<?> execute(String cid, PageAttributes attributes);
}
