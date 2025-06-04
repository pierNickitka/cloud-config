package ag.selmag.customer.client;

import ag.selmag.customer.client.exception.ClientBadRequestException;
import ag.selmag.customer.client.payload.NewProductReviewPayload;
import ag.selmag.customer.entity.ProductReview;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ProblemDetail;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class WebClientProductReviewsClient implements ProductReviewsClient {
  private final WebClient webCLient;

  @Override
  public Flux<ProductReview> findProductReviewsByProductId(Integer productId) {
    return this.webCLient.get()
            .uri("/feedback-api/product-reviews/by-product-id/{productId}", productId)
            .retrieve()
            .bodyToFlux(ProductReview.class);
  }

  @Override
  public Mono<ProductReview> createProductReview(Integer productId, Integer rating, String review) {
    return this.webCLient
            .post()
            .uri("/feedback-api/product-reviews")
            .bodyValue(new NewProductReviewPayload(productId, rating, review))
            .retrieve()
            .bodyToMono(ProductReview.class)
            .onErrorMap(WebClientResponseException.BadRequest.class,
                    ex -> new ClientBadRequestException("Возникла ошибка при добавление отзыва о товаре",ex,
                            ((List<String>) ex.getResponseBodyAs(ProblemDetail.class).getProperties().get("errors"))));
  }
}
