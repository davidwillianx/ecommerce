package com.dwx.ecommerce.products.adapter.output.persistence.dynamodb.core.converter;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.dwx.ecommerce.products.adapter.output.persistence.model.ProductDto;

import java.math.BigDecimal;
import java.util.Map;
import java.util.function.Function;

public class ProductConverter {

    private static final Function<Map<String, AttributeValue>, String> extractId = source -> source.get("PK").getS().split("#")[1];
    public static Map<String, AttributeValue> toDynamo(ProductDto product) {
        return Map.of(
                "PK", new AttributeValue().withS("PRODUCT#" + product.getId()),
                "SK", new AttributeValue().withS(product.getCode()),
                "product_price", new AttributeValue().withN(product.getPrice().toString())
        );
    }

    public static ProductDto fromDynamo(Map<String, AttributeValue> source) {
        return ProductDto.builder()
                .id(extractId.apply(source))
                .code(source.get("SK").getS())
                .price(new BigDecimal(source.get("product_price").getN()))
                .build();
    }
}
