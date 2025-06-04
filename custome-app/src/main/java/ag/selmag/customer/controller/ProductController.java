package ag.selmag.customer.controller;


import ag.selmag.customer.client.FavouriteProductsClient;
import ag.selmag.customer.client.ProductReviewsClient;
import ag.selmag.customer.client.ProductsClient;
import ag.selmag.customer.client.exception.ClientBadRequestException;
import ag.selmag.customer.controller.payload.NewProductReviewPayload;
import ag.selmag.customer.entity.Product;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.web.reactive.result.view.CsrfRequestDataValueProcessor;
import org.springframework.security.web.server.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.netty.http.server.HttpServerResponse;

import java.util.NoSuchElementException;

@Controller
@RequiredArgsConstructor
@RequestMapping("customer/products/{productId:\\d+}")
@Slf4j
public class ProductController {

  private final ProductsClient productsClient;

  private final FavouriteProductsClient favouriteProductClient;

  private final ProductReviewsClient productReviewsClient;

  @ModelAttribute(name = "product", binding = false)
  public Mono<Product> findProduct(@PathVariable("productId") int id) {
    return productsClient.findProduct(id)
            .switchIfEmpty(Mono.defer(
                    () -> Mono.error(new NoSuchElementException("customer.products.error.not_found"))
            ));
  }

  @GetMapping
  public Mono<String> getProductPage(@PathVariable("productId") Integer id,
                                     Model model) {
    model.addAttribute("inFavourite", false);
    return this.productReviewsClient.findProductReviewsByProductId(id)
            .collectList()
            .doOnNext(productReviews -> model.addAttribute("reviews", productReviews))
            .then(this.favouriteProductClient
                    .findFavouriteProductByProductId(id)
                    .doOnNext(fp -> model.addAttribute("inFavourite", true)))
            .thenReturn("customer/products/product");
  }

  @PostMapping("add-to-favourites")
  public Mono<String> addProductToFavourites(@ModelAttribute("product") Mono<Product> productMono) {
    return productMono
            .map(Product::id)
            .flatMap(productId -> this.favouriteProductClient.addProductToFavourites(productId)
                    .thenReturn("redirect:/customer/products/%d".formatted(productId))
                    .onErrorResume(exception -> {
                              log.error(exception.getMessage(), exception);
                              return Mono.just("redirect:/customer/products/%d".formatted(productId));
                            }
                    ));
  }

  @PostMapping("remove-from-favourites")
  public Mono<String> removeProductFromFavourites(@ModelAttribute("product") Mono<Product> productMono) {
    return productMono
            .map(Product::id)
            .flatMap(productId -> this.favouriteProductClient.removeProductFromFavourites(productId)
                    .thenReturn("redirect:/customer/products/%d".formatted(productId)));
  }

  @PostMapping("create-review")
  public Mono<String> createReview(@PathVariable("productId") int id,
                                   NewProductReviewPayload payload,
                                   Model model,
                                   ServerHttpResponse response) {
    return this.productReviewsClient.createProductReview(id, payload.rating(), payload.review())
            .thenReturn("redirect:/customer/products/%d".formatted(id))
            .onErrorResume(ClientBadRequestException.class, exception -> {
              model.addAttribute("inFavourite", false);
              model.addAttribute("payload", payload);
              model.addAttribute("errors", exception.getErrors());
              response.setStatusCode(HttpStatus.BAD_REQUEST);
              return this.favouriteProductClient.findFavouriteProductByProductId(id)
                      .doOnNext(pr -> model.addAttribute("inFavourite", true))
                      .thenReturn("customer/products/product");
            });
  }

  @ExceptionHandler(NoSuchElementException.class)
  public String handlerNoSuchElementException(NoSuchElementException e,
                                              Model model) {
    model.addAttribute("error", e.getMessage());
    return "errors/404";
  }

  @ModelAttribute
  public Mono<CsrfToken> loadCsrfToken(ServerWebExchange exchange){
    return exchange.<Mono<CsrfToken>>getAttribute(CsrfToken.class.getName())
            .doOnSuccess(token -> exchange.getAttributes()
              .put(CsrfRequestDataValueProcessor.DEFAULT_CSRF_ATTR_NAME, token));
  }
}
