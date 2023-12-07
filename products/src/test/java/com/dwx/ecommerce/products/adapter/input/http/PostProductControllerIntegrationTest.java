package com.dwx.ecommerce.products.adapter.input.http;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PostProductControllerIntegrationTest {
    @LocalServerPort
    Integer port;

    @BeforeEach
    void setUp() {
        RestAssured.baseURI = "http://localhost:" + port;
    }

    @Test
    void shouldReturnBadRequestWhenNameIsMissing() {
        final var payload = PostProductDto.builder()
                .code("00")
                .build();
        RestAssured.given()
                .get("/products")
                .then()
                .statusCode(200);

        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(payload)
                .header("tracking_id", "trackingId")
                .post("/products")
                .then()
                .statusCode(400);
    }


}