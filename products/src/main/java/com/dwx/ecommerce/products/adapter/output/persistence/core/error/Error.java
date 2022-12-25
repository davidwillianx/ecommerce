package com.dwx.ecommerce.products.adapter.output.persistence.core.error;

import lombok.Getter;

public  enum Error {
    UNEXPECTED_BEHAVIOR("EDB001"),
    RESOURCE_NOT_FOUND("EDB002"),
    TABLE_NOT_EXISTS("EDB003"),
    NON_INDENTIFIABLE_TRANSACTION("EDB004"),
    NON_UNIQUE_TRANSACTION("EDB005"),
    LIMIT_TRANSACTION_OVERPASS("EDB006"),
    NO_TRANSACTION_DEFINED("EDB007");



    @Getter
    private String code;
    Error(String code) {
       this.code = code;
    }

}
