package com.dwx.ecommerce.products.adapter.input.http;

import com.dwx.ecommerce.products.application.domain.Product;
import com.dwx.ecommerce.products.application.usecase.CreateProductUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

import java.lang.reflect.MalformedParametersException;
import java.net.URI;
import java.util.Objects;

@RequiredArgsConstructor
public class PostProductController {
    private final CreateProductUseCase useCase;

    public Mono<ResponseEntity<PostProductResponseDto>> execute(
            String trackingId,
            PostProductDto product
    ) {
        return Mono.just(product)
                .doOnNext(it -> {
                    if (Objects.isNull(it.getName())) {
                        throw new InvalidPayloadException("Missing required attribute");
                    }
                })
                .map(it -> Product.builder()
                        .code(it.getCode())
                        .description(it.getName())
                        .build()
                )
                .flatMap(it -> useCase.execute(trackingId, it))
                .map(it -> PostProductResponseDto.builder()
                        .code(it.getCode())
                        .name(it.getDescription())
                        .build())
                .map(it -> ResponseEntity.created(URI.create(""))
                        .body(it)
                )
                .onErrorResume(InvalidPayloadException.class, thrown -> Mono.just(ResponseEntity.badRequest().build()))
                .onErrorResume(RuntimeException.class, thrown -> Mono.just(ResponseEntity.internalServerError().build()));
    }
}
