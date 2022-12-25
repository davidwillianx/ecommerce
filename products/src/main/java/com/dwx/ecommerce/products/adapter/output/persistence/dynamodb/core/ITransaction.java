package com.dwx.ecommerce.products.adapter.output.persistence.dynamodb.core;

import com.dwx.ecommerce.products.adapter.output.persistence.core.command.Operation;
import com.dwx.ecommerce.products.adapter.output.persistence.dynamodb.core.domain.Model;
import com.dwx.ecommerce.products.adapter.output.persistence.dynamodb.core.domain.PK;
import reactor.core.publisher.Mono;

import java.util.function.Function;

public interface ITransaction<T> {
     Mono<T> findById(PK id, Function<Model, T> mapper);

     void add(Operation operation);

     Mono<Boolean> commit();
}
