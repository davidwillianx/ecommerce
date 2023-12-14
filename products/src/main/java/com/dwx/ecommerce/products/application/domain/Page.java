package com.dwx.ecommerce.products.application.domain;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class Page<T> {
    private T previous;
    private List<T> items;

    public static <T> Page<T> from(List<T> items) {
        return Page.<T>builder()
                .previous(items.get(0))
                .items(items)
                .build();
    }
}
