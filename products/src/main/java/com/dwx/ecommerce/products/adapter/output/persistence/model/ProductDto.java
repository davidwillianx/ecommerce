package com.dwx.ecommerce.products.adapter.output.persistence.model;

import lombok.*;

import java.math.BigDecimal;

@Builder
@Setter
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ProductDto {
    private String id;
    private String code;
    private BigDecimal price;
}
