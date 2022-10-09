package com.dwx.ecommerce.products.adapter.output.persistece.dynamodb;

import com.dwx.ecommerce.products.adapter.output.persistence.dynamodb.CidAlreadyInUseException;
import com.dwx.ecommerce.products.adapter.output.persistence.dynamodb.ProductDynamoDBRepository;
import com.dwx.ecommerce.products.adapter.output.persistence.dynamodb.ProductRepository;
import com.dwx.ecommerce.products.adapter.output.persistence.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ProductDynamoDBRepositoryUnitTest {
     ProductRepository sut;

     @BeforeEach
     void setUp(){
         sut = new ProductDynamoDBRepository();
     }

    @Test
    void shouldThrowErrorWhenIdempotencyAlreadyInUse() {
         final var cid = UUID.randomUUID().toString();
         final  var product = Product.builder().build();

        StepVerifier.create(sut.add(cid, product))
                .consumeErrorWith(thrown -> {
                    assertThat(thrown).isInstanceOf(CidAlreadyInUseException.class);
                }).verify();
    }

}