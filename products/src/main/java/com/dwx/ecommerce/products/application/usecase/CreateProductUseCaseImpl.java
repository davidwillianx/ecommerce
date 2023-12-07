package com.dwx.ecommerce.products.application.usecase;

import com.dwx.ecommerce.products.application.domain.Product;
import com.dwx.ecommerce.products.application.ports.database.ProductCreateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class CreateProductUseCaseImpl implements CreateProductUseCase {

    private final ProductCreateRepository repository;

    @Override
    public Mono<Product> execute(String trackingId, Product product) {
        return repository.execute(trackingId, product);
    }
}
