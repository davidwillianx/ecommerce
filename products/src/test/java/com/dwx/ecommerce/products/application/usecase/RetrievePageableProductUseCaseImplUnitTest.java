package com.dwx.ecommerce.products.application.usecase;


import com.dwx.ecommerce.products.application.domain.PageAttributes;
import com.dwx.ecommerce.products.application.domain.Product;
import com.dwx.ecommerce.products.application.domain.ProductCategory;
import com.dwx.ecommerce.products.application.ports.database.ProductMultipleRetrievalRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;

class RetrievePageableProductUseCaseImplUnitTest {
    RetrievePageableProductUseCase sut;
    ProductMultipleRetrievalRepository repository;

    @BeforeEach
    void setUp() {
        repository = Mockito.mock(ProductMultipleRetrievalRepository.class);
        sut = new RetrievePageableProductUseCaseImpl(repository);
    }

    @Test
    void shouldExecuteThrowErrorWhenItCouldNotGetValues() {
        final var cid = "cid";
        final var attributes = PageAttributes.builder()
                .code("00")
                .description("something")
                .productCategory(ProductCategory.FURNITURE)
                .build();

        BDDMockito.given(repository.execute(
                Mockito.anyString(),
                Mockito.any(PageAttributes.class)
        )).willReturn(Flux.error(new RuntimeException()));

        StepVerifier.create(sut.execute(cid, attributes))
                .consumeErrorWith(thrown -> {
                    assertThat(thrown).isNotNull();
                    BDDMockito.verify(repository).execute(cid, attributes);
                })
                .verify();
    }

    @Test
    void shouldExecuteReturnPreviousElement() {
        final var cid = "cid";
        final var attributes = PageAttributes.builder()
                .size(2)
                .code("00")
                .description("something")
                .productCategory(ProductCategory.FURNITURE)
                .build();

        final var product1 = Product.builder()
                .code("00")
                .build();
        final var product2 = Product.builder()
                .code("01")
                .build();

        final var product3 = Product.builder()
                .code("02")
                .build();

        BDDMockito.given(repository.execute(
                Mockito.anyString(),
                Mockito.any(PageAttributes.class)
        )).willReturn(Flux.just(product1, product2, product3));

        StepVerifier.create(sut.execute(cid, attributes))
                .assertNext(result -> {
                    assertThat(result).isNotNull();
                    assertThat(result.getPrevious()).isEqualTo(product1);
                })
                .verifyComplete();
    }

    @Test
    void shouldExecuteReturnPreviousAsNullWhenItIsTheFirstPage(){
        final var cid = "cid";
        final var attributes = PageAttributes.builder()
                .size(3)
                .code("00")
                .description("something")
                .productCategory(ProductCategory.FURNITURE)
                .build();

        final var product1 = Product.builder()
                .code("00")
                .build();
        final var product2 = Product.builder()
                .code("01")
                .build();

        final var product3 = Product.builder()
                .code("02")
                .build();

        BDDMockito.given(repository.execute(
                Mockito.anyString(),
                Mockito.any(PageAttributes.class)
        )).willReturn(Flux.just(product1, product2, product3));

        StepVerifier.create(sut.execute(cid, attributes))
                .assertNext(result -> {
                    assertThat(result).isNotNull();
                    assertThat(result.getPrevious()).isNull();
                })
                .verifyComplete();
    }

    @Test
    void shouldExecuteReturnPageContainingItemsMatchingPageSize() {
        final var cid = "cid";
        final var attributes = PageAttributes.builder()
                .size(3)
                .code("00")
                .description("something")
                .productCategory(ProductCategory.FURNITURE)
                .build();

        final var product1 = Product.builder()
                .code("00")
                .build();
        final var product2 = Product.builder()
                .code("01")
                .build();
        final var product3 = Product.builder()
                .code("02")
                .build();

        BDDMockito.given(repository.execute(
                Mockito.anyString(),
                Mockito.any(PageAttributes.class)
        )).willReturn(Flux.just(product1, product2, product3));

        StepVerifier.create(sut.execute(cid, attributes))
                .assertNext(result -> {
                    assertThat(result).isNotNull();
                    assertThat(result.getItems().size()).isEqualTo(3);
                })
                .verifyComplete();
    }

    @Test
    void shouldExecuteReturnNextIndex() {
        final var cid = "cid";
        final var attributes = PageAttributes.builder()
                .size(3)
                .code("00")
                .description("something")
                .productCategory(ProductCategory.FURNITURE)
                .build();

        final var product1 = Product.builder()
                .code("00")
                .build();
        final var product2 = Product.builder()
                .code("01")
                .build();
        final var product3 = Product.builder()
                .code("02")
                .build();

        final var product4 = Product.builder()
                .code("03")
                .build();

        BDDMockito.given(repository.execute(
                Mockito.anyString(),
                Mockito.any(PageAttributes.class)
        )).willReturn(Flux.just(product1, product2, product3, product4));

        StepVerifier.create(sut.execute(cid, attributes))
                .assertNext(result -> {
                    assertThat(result).isNotNull();
                    assertThat(result.getNext()).isNotNull();
                })
                .verifyComplete();
    }

    @Test
    void shouldExecuteReturnPageContainingItemsAndItsIndexes() {
        final var cid = "cid";
        final var attributes = PageAttributes.builder()
                .size(3)
                .code("00")
                .description("something")
                .productCategory(ProductCategory.FURNITURE)
                .build();

        final var product1 = Product.builder()
                .code("00")
                .build();
        final var product2 = Product.builder()
                .code("01")
                .build();
        final var product3 = Product.builder()
                .code("02")
                .build();
        final var product4 = Product.builder()
                .code("03")
                .build();

        BDDMockito.given(repository.execute(
                Mockito.anyString(),
                Mockito.any(PageAttributes.class)
        )).willReturn(Flux.just(product1, product2, product3, product4));

        StepVerifier.create(sut.execute(cid, attributes))
                .assertNext(result -> {
                    assertThat(result).isNotNull();
                    assertThat(result.getItems().size()).isEqualTo(2);
                    assertThat(result.getPrevious()).isEqualTo(product1);
                    assertThat(result.getNext()).isEqualTo(product4);
                })
                .verifyComplete();
    }


}