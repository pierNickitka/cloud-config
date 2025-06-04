package ag.selmag.feedback.controller;

import ag.selmag.feedback.controller.payload.NewProductReviewPayload;
import ag.selmag.feedback.entity.ProductReview;
import ag.selmag.feedback.service.ProductReviewsService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductReviewsRestControllerTest {

  @Mock
  ProductReviewsService service;

  @InjectMocks
  ProductReviewsRestController controller;

  @Test
  void findProductReviewsByProductId_ReturnsProductReviews(){
    //given
    doReturn(Flux.fromIterable(List.of(new ProductReview(UUID.fromString("6a8512d8-cbaa-11ee-b986-376cc5867cf5"),
            1, 5, "Хороший товар", "user-id1"), new ProductReview(UUID.fromString("2a8512d8-cbaa-41ee-b986-376cc5867cf5"),
            1, 3, "Так се товар", "user-id2"))))
            .when(this.service).findProductReviewsByProduct(1);
    //when
    StepVerifier.create(this.controller.findProductReviewsByProductId(1))
            .expectNext(new ProductReview(UUID.fromString("6a8512d8-cbaa-11ee-b986-376cc5867cf5"),
                    1, 5, "Хороший товар", "user-id1"), new ProductReview(UUID.fromString("2a8512d8-cbaa-41ee-b986-376cc5867cf5"),
                    1, 3, "Так се товар", "user-id2"))
            .verifyComplete();
    //then
    verify(this.service).findProductReviewsByProduct(1);
    verifyNoInteractions(this.service);
  }


  @Test
  void createProductReview_RequestIsValid_ReturnsNewProductReview(){
    var payload = Mono.just(new NewProductReviewPayload(1, 5, "Отличный товар"));
    var uriComponentsBuilder = UriComponentsBuilder.fromUriString("http://localhost/");
    var token = Mono.just(new JwtAuthenticationToken(Jwt.withTokenValue("e30.e30")
            .headers(headers -> headers.put("foo", "bar"))
            .claim("sub", "5f1d5cf8-cbd6-11ee-9579-cf24d050b47c").build()));

    doReturn(Mono.just(new ProductReview(UUID.fromString("6a8512d8-cbaa-11ee-b986-376cc5867cf5"),
            1, 5, "Хороший товар", "user-id1"))).when(this.service)
            .createProductReview(1,5,"Отличный товар", "5f1d5cf8-cbd6-11ee-9579-cf24d050b47c");

    StepVerifier.create(this.controller.createProductReview(token,payload,uriComponentsBuilder))
            .expectNext(ResponseEntity.created(URI.create("http://localhost/feedback-api/product-reviews/6a8512d8-cbaa-11ee-b986-376cc5867cf5"))
                    .body(new ProductReview(UUID.fromString("6a8512d8-cbaa-11ee-b986-376cc5867cf5"),
                            1, 5, "Хороший товар", "user-id1")))
            .verifyComplete();

    verify(this.service).createProductReview(1,5,"Отличный товар",
            "5f1d5cf8-cbd6-11ee-9579-cf24d050b47c");
    verifyNoMoreInteractions(this.service);

  }
}