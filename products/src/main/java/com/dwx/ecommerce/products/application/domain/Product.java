package com.dwx.ecommerce.products.application.domain;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Builder
@Getter
public class Product {
    private String id;
    private String code;
    private BigDecimal price;
}
