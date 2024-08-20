package com.dwx.ecommerce.products.application.usecase;

import com.dwx.ecommerce.products.application.domain.Page;
import com.dwx.ecommerce.products.application.domain.PageAttributes;
import com.dwx.ecommerce.products.application.domain.Product;
import reactor.core.publisher.Mono;

public interface RetrievePageableProductUseCase {
    Mono<Page<Product>> execute(String cid, PageAttributes pageAttributes);
}
