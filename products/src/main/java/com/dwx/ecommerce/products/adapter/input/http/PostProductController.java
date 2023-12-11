package com.dwx.ecommerce.products.adapter.input.http;

import com.dwx.ecommerce.products.application.domain.Product;
import com.dwx.ecommerce.products.application.usecase.CreateProductUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Objects;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class PostProductController {
    private final CreateProductUseCase useCase;

    @PostMapping
    public Mono<ResponseEntity<PostProductResponseDto>> execute(
            @RequestHeader("tracking_id") String trackingId,
            @RequestBody PostProductDto product
    ) {
        return Mono.just(product)
                .doOnNext(it -> {
                    if (Objects.isNull(it.getName())) {
                        throw new InvalidPayloadException("Missing required attribute");
                    }
                })
                .doOnNext(it -> {
                    if (Objects.isNull(it.getCategory())) {
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
