package com.dwx.ecommerce.products.adapter.output.persistence.dynamodb.core;

import com.dwx.ecommerce.products.adapter.output.persistence.dynamodb.core.domain.Model;
import com.dwx.ecommerce.products.adapter.output.persistence.dynamodb.core.domain.PK;
import reactor.core.publisher.Mono;

import java.util.function.Function;

public interface ITransaction<T, O>  {
     Mono<T> findById(PK id, Function<Model, T> mapper);

     void add(O operation);

     Mono<Boolean> commit();
}
