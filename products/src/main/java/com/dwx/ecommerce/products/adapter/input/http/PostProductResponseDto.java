package com.dwx.ecommerce.products.adapter.input.http;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PostProductResponseDto {
    private String code;
    private String name;
}
