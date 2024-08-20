package com.dwx.ecommerce.products.adapter.input.http;

import com.dwx.ecommerce.products.application.domain.Page;
import com.dwx.ecommerce.products.application.domain.PageAttributes;
import com.dwx.ecommerce.products.application.domain.Product;
import com.dwx.ecommerce.products.application.domain.ProductCategory;
import com.dwx.ecommerce.products.application.usecase.RetrievePageableProductUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class GetProductControllerUnitTest {

    GetProductController sut;
    RetrievePageableProductUseCase useCase;


    @BeforeEach
    void setUp() {
        useCase = Mockito.mock(RetrievePageableProductUseCase.class);
        sut = new GetProductController(useCase);
    }

    @Test
    void shouldReturnEmptyWhenQueryDoesNotMatchAnyValue() {
        final var cid = "cid";
        final var code = "0000";
        final var description = "NotAbleToFindItem";
        final var category = CategoryDto.FURNITURE;

        BDDMockito.given(useCase.execute(
                        Mockito.anyString(),
                        Mockito.any(PageAttributes.class)
                ))
                .willReturn(Mono.just(Page.<Product>builder()
                                .items(Collections.emptyList())
                        .build()));

        StepVerifier.create(sut.execute(
                        cid,
                        code,
                        description,
                        category
                ))
                .assertNext((ResponseEntity<?> result) -> {
                    assertThat(result).isNotNull();
                    final var page = (PageDto) result.getBody();
                    assertThat(page.getItems().isEmpty()).isTrue();
                })
                .verifyComplete();
    }

    @Test
    void shouldReturnErrorWhenItCouldNotGetData() {
        final var cid = "cid";
        final var nextCursor = "0000";
        final var description = "NotAbleToFindItem";
        final var category = CategoryDto.FURNITURE;

        BDDMockito.given(useCase.execute(
                        Mockito.anyString(),
                        Mockito.any(PageAttributes.class)
                ))
                .willReturn(Mono.error(new RuntimeException()));

        StepVerifier.create(sut.execute(
                        cid,
                        nextCursor,
                        description,
                        category
                ))
                .assertNext((ResponseEntity result) -> {
                    assertThat(result).isNotNull();
                    assertThat(result.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
                    Mockito.verify(useCase).execute(
                            Mockito.eq(cid),
                            Mockito.argThat(it -> {
                                assertThat(it.getNextCursor()).isEqualTo(nextCursor);
                                assertThat(it.getDescription()).isEqualTo(description);
                                assertThat(it.getProductCategory()).isEqualTo(ProductCategory.FURNITURE);
                                return Boolean.TRUE;
                            })
                    );
                })
                .verifyComplete();
    }

    @Test
    void shouldExecuteReturnPageContainingElements() {
        final var cid = "cid";
        final var code = "0000";
        final var description = "NotAbleToFindItem";
        final var category = CategoryDto.FURNITURE;
        final var product1 = Product.builder()
                .category(ProductCategory.FURNITURE)
                .code("00")
                .description("something")
                .build();

        final var product2 = Product.builder()
                .category(ProductCategory.FURNITURE)
                .code("00")
                .description("something")
                .build();

        BDDMockito.given(useCase.execute(
                        Mockito.anyString(),
                        Mockito.any(PageAttributes.class)
                ))
                .willReturn(Mono.just(Page.<Product>builder()
                        .items(List.of(product1, product2))
                        .build()));

        StepVerifier.create(sut.execute(
                        cid,
                        code,
                        description,
                        category
                ))
                .assertNext(result -> {
                    assertThat(result).isNotNull();
                    final var page = (PageDto) result.getBody();

                    assertThat(page.getItems()).isNotEmpty();
                    assertThat(page.getItems().size()).isEqualTo(2);
                })
                .verifyComplete();
    }

}