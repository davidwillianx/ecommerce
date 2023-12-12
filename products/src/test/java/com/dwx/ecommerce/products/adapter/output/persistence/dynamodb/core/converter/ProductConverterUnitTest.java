package com.dwx.ecommerce.products.adapter.output.persistence.dynamodb.core.converter;


import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.dwx.ecommerce.products.adapter.output.persistence.model.ProductDto;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;


class ProductConverterUnitTest {

    @Test
    void shouldMapToDynamo() {
        final var product = ProductDto.builder()
                .id("id")
                .code("code")
                .price(BigDecimal.ONE)
                .build();

        final var result = ProductConverter.toDynamo(product);
        assertThat(result).isNotNull();
        assertThat(result.get("PK").getS()).isEqualTo("PRODUCT#id");
        assertThat(result.get("SK").getS()).isEqualTo("code");
        assertThat(result.get("product_price").getN()).isEqualTo("1");
    }

    @Test
    void shouldMapToEntity() {
        final var source = new HashMap<>(Map.of(
                "PK", new AttributeValue("PRODUCT#id"),
                "SK", new AttributeValue("code"),
                "product_price", new AttributeValue().withN("10.00")
        ));

        final var result = ProductConverter.fromDynamo(source);
        assertThat(result.getId()).isEqualTo("id");
        assertThat(result.getCode()).isEqualTo("code");
        assertThat(result.getPrice()).isEqualTo( new BigDecimal("10.00", MathContext.UNLIMITED));
    }

}