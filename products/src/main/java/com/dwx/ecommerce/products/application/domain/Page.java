package com.dwx.ecommerce.products.application.domain;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class Page<T> {
    private T previous;
    private List<T> items;

    public static <T> Page<T> from(Integer size, List<T> items) {
        final var prev = items.size() > size ? items.get(0): null;
        final var pageItems = items.stream()
                .filter(it -> !it.equals(prev))
                .toList();

        return Page.<T>builder()
                .previous(prev)
                .items(pageItems)
                .build();
    }
}
