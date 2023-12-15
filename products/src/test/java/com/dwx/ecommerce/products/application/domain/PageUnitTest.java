package com.dwx.ecommerce.products.application.domain;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PageUnitTest {

   @Test
    void shouldFromHandleFirstPage() {
       final var product1 = Product.builder()
               .code("00")
               .build();
       final var product2 = Product.builder()
               .code("01")
               .build();

       final var items = List.of(product1, product2);

       final var result = Page.from(
               null,
               2,
               items,
               (Product p, String prevIndex) -> p.getCode().equals(prevIndex)
       );

       assertThat(result.getPrevious()).isNull();
       assertThat(result.getItems().size()).isEqualTo(2);
   }

    @Test
    void shouldFromSeparatePrevIndexFromItems() {
        final var product1 = Product.builder()
                .code("00")
                .build();
        final var product2 = Product.builder()
                .code("01")
                .build();
        final var product3 = Product.builder()
                .code("01")
                .build();

        final var items = List.of(product1, product2, product3);

        final var result = Page.from(
                "00",
                2,
                items,
                (Product p, String prevIndex) -> p.getCode().equals(prevIndex)
        );

        assertThat(result.getPrevious()).isEqualTo(product1);
        assertThat(result.getItems()).doesNotContain(product1);
    }
    @Test
    void shouldFromHandlesNextCursor() {
        final var product1 = Product.builder()
                .code("00")
                .build();
        final var product2 = Product.builder()
                .code("01")
                .build();
        final var product3 = Product.builder()
                .code("01")
                .build();

        final var items = List.of(product1, product2, product3);
        final var result = Page.from(
                null,
                2,
                items,
                (Product p, String prevIndex) -> p.getCode().equals(prevIndex)
        );

        assertThat(result.getNext()).isEqualTo(product3);
        assertThat(result.getItems()).doesNotContain(product3);
    }

    @Test
    void shouldFromHandleNextAndPrevCursor(){
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

        final var items = List.of(product1, product2, product3, product4);
        final var result = Page.from(
                "00",
                2,
                items,
                (Product p, String prevIndex) -> p.getCode().equals(prevIndex)
        );

        assertThat(result.getNext()).isEqualTo(product4);
        assertThat(result.getPrevious()).isEqualTo(product1);
    }

    @Test
    void shouldFromHandleSinglePage() {
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

        final var items = List.of(product1, product2, product3, product4);
        final var result = Page.from(
                null,
                4,
                items,
                (Product p, String prevIndex) -> p.getCode().equals(prevIndex)
        );

        assertThat(result.getNext()).isNull();
        assertThat(result.getPrevious()).isNull();
    }

}