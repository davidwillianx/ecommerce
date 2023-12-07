package com.dwx.ecommerce.products.application.usecase;


import com.dwx.ecommerce.products.application.domain.Product;
import com.dwx.ecommerce.products.application.ports.database.ProductCreateRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class CreateProductUseCaseImplUnitTest {
    CreateProductUseCase sut;
    ProductCreateRepository repository;

    @BeforeEach
    void setUp() {
        repository = Mockito.mock(ProductCreateRepository.class);
        sut = new CreateProductUseCaseImpl(repository);
    }

    @Test
    void shouldExecuteRunSuccessfully() {
        final var trackingId = "trackingId";
        final var product = Product.builder()
                .code("code")
                .price(BigDecimal.valueOf(23.00))
                        .build();

        BDDMockito.given(repository.execute(
                Mockito.anyString(),
                Mockito.any(Product.class)
        )).willReturn(Mono.just(
                Product.builder().build()
        ));

        StepVerifier.create(sut.execute(trackingId, product))
                .assertNext(Assertions::assertNotNull)
                .verifyComplete();
    }

    @Test
    void shouldExecuteSaveProduct() {
        final var trackingId = "trackingId";
        final var product = Product.builder()
                .code("code")
                .price(BigDecimal.valueOf(23.00))
                .build();

        BDDMockito.given(repository.execute(
                Mockito.anyString(),
                Mockito.any(Product.class)
        )).willReturn(Mono.just(
                Product.builder().build()
        ));

        StepVerifier.create(sut.execute(trackingId, product))
                .assertNext(result -> {
                    assertThat(result).isNotNull();
                    Mockito.verify(repository).execute(trackingId, product);
                })
                .verifyComplete();
    }

}