package com.dwx.ecommerce.products.application.domain;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@Builder
@EqualsAndHashCode
public class PageAttributes {
    private String code;
    private String description;
    private ProductCategory productCategory;
    private Integer size;
    private String nextIndex;

    public static PageAttributes from(
            String code,
            String description,
            ProductCategory category
    ) {
        return PageAttributes.builder()
                .code(code)
                .description(description)
                .productCategory(category)
                .build();
    }
}
