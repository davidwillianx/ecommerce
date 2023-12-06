package com.dwx.ecommerce.products.adapter.input.http;

import com.dwx.ecommerce.products.application.domain.Product;
import com.dwx.ecommerce.products.application.usecase.CreateProductUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;

class PostProductControllerUnitTest {
    PostProductController sut;
    CreateProductUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = Mockito.mock(CreateProductUseCase.class);
        sut = new PostProductController(useCase);
    }

    @Test
    void shouldExecuteThrowErrorWhenNameIsMissing() {
        final var product = PostProductDto.builder()
                .build();

        StepVerifier.create(sut.execute("trackingId", product))
                .assertNext(result -> {
                    assertThat(result).isNotNull();
                    assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
                })
                .verifyComplete();
    }

    @Test
    void shouldExecuteThrowErrorWhenTryToRegisterProduct() {
        final var product = PostProductDto.builder()
                .code("00")
                .name("Smartphone")
                .build();

        BDDMockito.given(useCase.execute(
                        Mockito.anyString(),
                        Mockito.any()
                ))
                .willReturn(Mono.error(new RuntimeException()));

        StepVerifier.create(sut.execute("trackingId", product))
                .assertNext(result -> {
                    assertThat(result).isNotNull();
                    assertThat(result.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);

                    Mockito.verify(useCase).execute(
                            "trackingId",
                            Product.builder()
                                    .code("00")
                                    .description("Smartphone")
                                    .build()
                    );
                })
                .verifyComplete();
    }

    @Test
    void shouldExecuteReturnSuccessfully() {
        final var product = PostProductDto.builder()
                .code("00")
                .name("Smartphone")
                .build();

        BDDMockito.given(useCase.execute(
                        Mockito.anyString(),
                        Mockito.any()
                ))
                .willReturn(Mono.just(Product.builder()
                        .description("Smartphone")
                        .code("00")
                        .build()));

        StepVerifier.create(sut.execute("trackingId", product))
                .assertNext(result -> {
                    assertThat(result).isNotNull();
                    assertThat(result.getStatusCode()).isEqualTo(HttpStatus.CREATED);

                    assertThat(result.getBody().getCode()).isEqualTo("00");
                    assertThat(result.getBody().getName()).isEqualTo("Smartphone");
                })
                .verifyComplete();
    }


}