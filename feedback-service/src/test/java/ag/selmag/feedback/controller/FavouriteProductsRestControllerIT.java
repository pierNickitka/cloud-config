package ag.selmag.feedback.controller;

import ag.selmag.feedback.entity.FavouriteProduct;
import ag.selmag.feedback.entity.ProductReview;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;


import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.mockJwt;


@SpringBootTest
@AutoConfigureWebTestClient
class FavouriteProductsRestControllerIT {

  @Autowired
  WebTestClient webClient;

  @Autowired
  ReactiveMongoTemplate mongoTemplate;

  @BeforeEach
  void setUp() {
    this.mongoTemplate.insertAll(List.of(new FavouriteProduct
                            (UUID.fromString("bd7779c3-cb05-11ee-b5f3-df46a1249898"), 1, "user-id-1"),
                    new FavouriteProduct
                            (UUID.fromString("cd7779c3-cb05-11ee-b5f3-df46a1249898"), 1, "user-id-2"),
                    new FavouriteProduct
                            (UUID.fromString("cd7779c4-cb05-11ee-b5f3-df46a1249898"), 1, "user-id-3")))
            .blockLast();
  }

  @AfterEach
  void tearDown() {
    this.mongoTemplate.remove(FavouriteProduct.class).all().block();
  }

  @Test
  void findFavouriteProducts_ReturnsFavouriteProducts() throws Exception {
    webClient.mutateWith(mockJwt().jwt(builder -> builder.subject("user-id-1")))
            .get()
            .uri("/feedback-api/favourite-products")
            .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
            .exchange()
            .expectStatus().isOk()
            .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
            .expectBody().json("""
                    [
                    {"id": "bd7779c3-cb05-11ee-b5f3-df46a1249898","productId": 1, "userId": "user-id-1"}
                    ]""");
  }

  @Test
  void findFavouriteProductByProductId_ReturnsFavouriteProduct() throws Exception {
    this.webClient
            .mutateWith(mockJwt().jwt(builder -> builder.subject("user-id-1")))
            .get()
            .uri("/feedback-api/favourite-products/by-product-id/1")
            .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
            .exchange()
            .expectStatus().isOk()
            .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
            .expectBody().json(
                    """
                    {"id": "bd7779c3-cb05-11ee-b5f3-df46a1249898","productId": 1, "userId": "user-id-1"}
                    """);

  }

  @Test
  void addProductToFavourite_RequestIsValid_ReturnsFavouriteProduct() throws Exception {
    this.webClient
            .mutateWith(mockJwt().jwt(builder -> builder.subject("user-id-1")))
            .post()
            .uri("/feedback-api/favourite-products")
            .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue("""
                    {"productId": 1}
                    """)
            .exchange()
            .expectStatus().isCreated()
            .expectHeader().exists(HttpHeaders.LOCATION)
            .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
            .expectBody().json(
                    """
                    {"productId": 1, "userId": "user-id-1"}
                    """).jsonPath("$.id").exists();
  }

  @Test
  void addProductToFavourite_RequestIsInvalid_ReturnsFavouriteProduct() throws Exception {
    this.webClient
            .mutateWith(mockJwt().jwt(builder -> builder.subject("user-id-1")))
            .post()
            .uri("/feedback-api/favourite-products")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue("""
                    {"productId": null}
                    """)
            .exchange()
            .expectStatus().isBadRequest()
            .expectHeader().doesNotExist(HttpHeaders.LOCATION)
            .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON)
            .expectBody().json(
                    """
                    {"errors":  ["Товар не указан"]}
                    """);
  }

  @Test
  void addProductToFavourite_UserNotAuthentication_ReturnsUserUnauthorizedStatus() throws Exception {
    this.webClient
            .post()
            .uri("/feedback-api/favourite-products")
            .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue("""
                    {"productId": 1}
                    """)
            .exchange()
            .expectStatus().isUnauthorized();
  }

}