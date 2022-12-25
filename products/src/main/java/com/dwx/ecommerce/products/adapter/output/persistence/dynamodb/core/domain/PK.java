package com.dwx.ecommerce.products.adapter.output.persistence.dynamodb.core.domain;

import lombok.Builder;
import lombok.Getter;

import java.util.Map;
@Builder
@Getter
public class PK {
    private String PK;
    private String SK;

    //TODO: Move to an interface or abstract class
    public Map<String, Object> getId() {
        return Map.of(
                "PK", this.PK,
                 "SK", this.SK
        );
    }
}
