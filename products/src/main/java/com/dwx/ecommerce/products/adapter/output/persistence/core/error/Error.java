package com.dwx.ecommerce.products.adapter.output.persistence.dynamodb.core.error;

import lombok.Getter;

public  enum Error {
    UNEXPECTED_BEHAVIOR("EDB001"),
    RESOURCE_NOT_FOUND("EDB002"),
    TABLE_NOT_EXISTS("EDB003");



    @Getter
    private String code;
    Error(String code) {
       this.code = code;
    }

}
