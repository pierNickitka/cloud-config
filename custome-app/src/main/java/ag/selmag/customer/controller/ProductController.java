package ag.selmag.customer.controller;


import ag.selmag.customer.client.FavouriteProductsClient;
import ag.selmag.customer.client.ProductReviewsClient;
import ag.selmag.customer.client.ProductsClient;
import ag.selmag.customer.client.exception.ClientBadRequestException;
import ag.selmag.customer.controller.payload.NewProductReviewPayload;
import ag.selmag.customer.entity.Product;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

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
            .switchIfEmpty(Mono.error(new NoSuchElementException("customer.products.errors.not_found")));
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
                                   Model model) {
    return this.productReviewsClient.createProductReview(id, payload.rating(), payload.review())
            .thenReturn("redirect:/customer/products/%d".formatted(id))
            .onErrorResume(ClientBadRequestException.class, exception -> {
              model.addAttribute("inFavourite", false);
              model.addAttribute("payload", payload);
              model.addAttribute("errors", exception.getErrors());
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
}
