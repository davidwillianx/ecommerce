package com.dwx.ecommerce.products.application.usecase;

import com.dwx.ecommerce.products.application.domain.Page;
import com.dwx.ecommerce.products.application.domain.PageAttributes;
import com.dwx.ecommerce.products.application.domain.Product;
import com.dwx.ecommerce.products.application.ports.database.ProductMultipleRetrievalRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class RetrievePageableProductUseCaseImpl implements RetrievePageableProductUseCase {

    private final ProductMultipleRetrievalRepository repository;

    @Override
    public Mono<Page<Product>> execute(String cid, PageAttributes pageAttributes) {
        return repository.execute(cid, pageAttributes)
                .collectList()
                .map(items -> Page.from(
                        pageAttributes.getNextCursor(),
                        pageAttributes.getSize(),
                        items,
                        (Product p, String index) -> p.getCode().equalsIgnoreCase(index)
                ));
    }
}
