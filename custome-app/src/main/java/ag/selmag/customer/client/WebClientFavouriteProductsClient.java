package ag.selmag.customer.client;

import ag.selmag.customer.client.exception.ClientBadRequestException;
import ag.selmag.customer.client.payload.NewFavouriteProductPayload;
import ag.selmag.customer.controller.payload.NewProductReviewPayload;
import ag.selmag.customer.entity.FavouriteProduct;
import ag.selmag.customer.entity.ProductReview;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ProblemDetail;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RequiredArgsConstructor
public class WebClientFavouriteProductsClient implements FavouriteProductsClient {

  private final WebClient webClient;

  @Override
  public Flux<FavouriteProduct> findFavouriteProducts() {
    return this.webClient.get()
            .uri("/feedback-api/favourite-products")
            .retrieve()
            .bodyToFlux(FavouriteProduct.class);
  }

  @Override
  public Mono<FavouriteProduct> findFavouriteProductByProductId(int id) {
    return this.webClient.get()
            .uri("/feedback-api/favourite-products/by-product-id/{productId}" ,id)
            .retrieve()
            .bodyToMono(FavouriteProduct.class)
            .onErrorComplete(WebClientResponseException.NotFound.class);
  }

  @Override
  public Mono<FavouriteProduct> addProductToFavourites(Integer productId) {
    return this.webClient.post()
            .uri("/feedback-api/favourite-products")
            .bodyValue(new NewFavouriteProductPayload(productId))
            .retrieve()
            .bodyToMono(FavouriteProduct.class)
            .onErrorMap(WebClientResponseException.BadRequest.class,
                    ex -> new ClientBadRequestException("Возникла ошибка при добавление товара в избранные",ex,
                            ((List<String>) ex.getResponseBodyAs(ProblemDetail.class)
                                    .getProperties().get("errors"))));
  }

  @Override
  public Mono<Void> removeProductFromFavourites(Integer productId) {
    return this.webClient.delete()
            .uri("/feedback-api/favourite-products/by-product-id/{productId}" ,productId)
            .retrieve()
            .toBodilessEntity()
            .then();
  }
}
