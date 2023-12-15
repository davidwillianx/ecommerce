package com.dwx.ecommerce.products.application.domain;

import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.Objects;
import java.util.function.BiPredicate;

@Getter
@Builder
public class Page<T> {
    private T previous;
    private T next;
    private List<T> items;

    public static <T> Page<T> from(
            String prevIndex,
            Integer size,
            List<T> items,
            BiPredicate<T, String> keyComparator
    ) {
        final var prev = items.stream()
                .filter(it -> keyComparator.test(it, prevIndex))
                .findFirst()
                .orElse(null);

        final var hasPrevAndNext = Objects.nonNull(prev) && items.size() > size + 1;
        final var hasNextOnly = Objects.isNull(prev) && items.size() > size;

        final var next =  hasNextOnly || hasPrevAndNext
                ? items.get(items.size() - 1)
                : null;

        final var pageItems = items.stream()
                .filter(it -> !it.equals(prev))
                .filter(it -> !it.equals(next))
                .toList();

        return Page.<T>builder()
                .previous(prev)
                .next(next)
                .items(pageItems)
                .build();
    }
}
