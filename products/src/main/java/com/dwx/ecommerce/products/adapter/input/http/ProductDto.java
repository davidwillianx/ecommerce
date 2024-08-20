package com.dwx.ecommerce.products.adapter.input.http;

import com.dwx.ecommerce.products.application.domain.Product;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ProductDto {
    private String code;
    private String description;
    private CategoryDto categoryDto;

    public static ProductDto from(Product item) {
        return ProductDto.builder()
                .code(item.getCode())
                .description(item.getDescription())
                .categoryDto(CategoryDto.FURNITURE)
                .build();
    }
}
