package com.dwx.ecommerce.products.application.domain;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.math.BigDecimal;

@Builder
@Getter
@EqualsAndHashCode
public class Product {
    private String id;
    private String code;
    private String description;
    private BigDecimal price;
}
