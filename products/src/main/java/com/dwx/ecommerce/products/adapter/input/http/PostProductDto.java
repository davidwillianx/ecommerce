package com.dwx.ecommerce.products.adapter.input.http;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PostProductDto {
    private String code;
    private String name;
    private CategoryDto category;
}
