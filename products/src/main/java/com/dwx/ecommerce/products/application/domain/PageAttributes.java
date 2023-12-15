package com.dwx.ecommerce.products.application.domain;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@Builder
@EqualsAndHashCode
public class PageAttributes {
    private String description;
    private ProductCategory productCategory;
    private Integer size;
    private String nextCursor;

    public static PageAttributes from(
            String nextIndex,
            String description,
            ProductCategory category
    ) {
        return PageAttributes.builder()
                .nextCursor(nextIndex)
                .description(description)
                .productCategory(category)
                .build();
    }
}
