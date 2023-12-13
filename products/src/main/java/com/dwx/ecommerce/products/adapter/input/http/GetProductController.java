package com.dwx.ecommerce.products.adapter.input.http;

import com.dwx.ecommerce.products.application.domain.PageAttributes;
import com.dwx.ecommerce.products.application.usecase.RetrievePageableProductUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;



@RequiredArgsConstructor
public class GetProductController {

    private final RetrievePageableProductUseCase useCase;

    public Mono<ResponseEntity<PageDto<ProductDto>>> execute(
            String cid,
            String code,
            String description,
            CategoryDto categoryDto
    ) {
        return Mono.just(categoryDto)
                .map(CategoryDto::from)
                .map(category -> PageAttributes.from(code, description, category))
                .flatMap(it -> Mono.defer(() -> useCase.execute(cid, it)))
                .map(it -> PageDto.from(it, ProductDto::from))
                .map(ResponseEntity::ok)
                .onErrorResume(
                        RuntimeException.class,
                        thrown -> Mono.just(
                                ResponseEntity.internalServerError()
                                        .build()
                        )
                );
    }
}
