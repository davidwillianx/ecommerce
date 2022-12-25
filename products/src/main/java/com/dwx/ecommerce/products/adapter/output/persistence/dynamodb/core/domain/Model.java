package com.dwx.ecommerce.products.adapter.output.persistence.dynamodb.core.domain;

import java.util.Map;

public interface Model {
    PK getId();
    Map<String, Object> getItem();
}
