package com.dwx.ecommerce.products.application.usecase;


import com.dwx.ecommerce.products.application.domain.PageAttributes;
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



}