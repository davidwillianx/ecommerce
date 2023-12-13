package com.dwx.ecommerce.products.adapter.input.http;

import com.dwx.ecommerce.products.application.domain.Page;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.function.Function;

@Getter
@Builder
public class PageDto<T> {

    private List<T> items;

    public  static <T,N> PageDto<T> from(Page<N> page, Function<N, T> converter) {
        final var items = page.getItems().stream()
                .map(converter)
                .toList();

        return PageDto.<T>builder()
                .items(items)
                .build();
    }
}
