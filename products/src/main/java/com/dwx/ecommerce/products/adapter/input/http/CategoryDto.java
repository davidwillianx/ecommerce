package com.dwx.ecommerce.products.adapter.input.http;

import com.dwx.ecommerce.products.application.domain.ProductCategory;

public enum CategoryDto {
    ELECTRONIC,
    FURNITURE;

    public static ProductCategory from(CategoryDto dto) {
        return ProductCategory.valueOf(dto.name());
    }
}
