package com.dwx.ecommerce.products.application.domain;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PageAttributes {
    private String code;
    private String description;
    private ProductCategory productCategory;

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
