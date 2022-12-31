package com.dwx.ecommerce.products.adapter.output.persistence.dynamodb.core;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBAsync;
import com.amazonaws.services.dynamodbv2.document.ItemUtils;
import com.amazonaws.services.dynamodbv2.model.PutItemRequest;
import com.dwx.configs.LocalstackInitializer;
import com.dwx.ecommerce.products.adapter.output.persistence.config.RepositoryInitializer;
import com.dwx.ecommerce.products.adapter.output.persistence.dynamodb.core.command.DynamoWriteOperation;
import com.dwx.ecommerce.products.adapter.output.persistence.dynamodb.core.domain.PK;
import com.dwx.ecommerce.products.config.aws.AwsInitializer;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import reactor.test.StepVerifier;

import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


@SpringBootTest
@ContextConfiguration(classes = {
        AwsInitializer.class,
        LocalstackInitializer.class,
        RepositoryInitializer.class
})
class TransactionIntegrationTest {

    @Autowired
    ITransaction<Domain> sut;

    @Autowired
    AmazonDynamoDBAsync dynamoDB;

    @Nested
    class FindByIdTest {

        @Test
        void shouldQueryByPK() {
            final var pk = PK.builder()
                    .PK("MyKEY")
                    .SK("MYSK")
                    .build();

            final var source = ItemUtils.fromSimpleMap(Map.of(
                    "PK", "MyKEY",
                    "SK", "MYSK",
                    "name", "ThisIsMyName",
                    "surname", "ThisIsMySurname"
            ));

            final var putItem = new PutItemRequest()
                    .withItem(source);

            dynamoDB.putItem(putItem);

            StepVerifier.create(sut.findById(pk, model -> {
                final var domain = new Domain();
                domain.name = model.getItem().get("name").toString();
                domain.surname = model.getItem().get("surname").toString();

                return domain;
            }))
                    .assertNext(result -> {
                        assertThat(result).isNotNull();
                        assertThat(result.name).isEqualTo("ThisIsMyName");
                        assertThat(result.surname).isEqualTo("ThisIsMySurname");
                    })
                    .verifyComplete();
        }

    }


    static class Domain {
        private String name;
        private String surname;
    }

}