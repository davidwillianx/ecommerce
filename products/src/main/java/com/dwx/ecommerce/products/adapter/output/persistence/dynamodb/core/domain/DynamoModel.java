package com.dwx.ecommerce.products.adapter.output.persistence.dynamodb.core.domain;

import com.amazonaws.services.dynamodbv2.document.ItemUtils;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import lombok.*;

import java.util.Map;

@Builder
@Setter
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class DynamoModel implements Model {
    private PK id;
    private Map<String, Object> item;


    public static DynamoModel fromSource(Map<String, AttributeValue> item) {
        return DynamoModel.builder()
                .item(ItemUtils.toSimpleMapValue(item))
                .build();
    }
}
