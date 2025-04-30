package ag.selmag.feedback.controller;


import ag.selmag.feedback.controller.payload.NewFavouriteProductPayload;
import ag.selmag.feedback.entity.FavouriteProduct;
import ag.selmag.feedback.service.FavouriteProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("feedback-api/favourite-products")
@RequiredArgsConstructor
public class FavouriteProductsRestController {

  private final FavouriteProductService productService;

  @GetMapping
  public Flux<FavouriteProduct> findFavouriteProducts() {
    return this.productService.findFavouriteProducts();
  }

  @GetMapping("by-product-id/{productId:\\d+}")
  public Mono<FavouriteProduct> findFavouriteProductByProductId(@PathVariable("productId") int id) {
    return this.productService.findFavouriteProductByProduct(id);
  }

  @PostMapping
  public Mono<ResponseEntity<FavouriteProduct>> createFavouriteProduct(
          @Valid @RequestBody Mono<NewFavouriteProductPayload> payloadMono,
          UriComponentsBuilder uriComponentsBuilder) {
    return payloadMono
            .flatMap(favouritePayloadMono -> this.productService.addProductToFavourites(favouritePayloadMono.productId()))
            .map(favouriteProduct -> ResponseEntity
                    .created(uriComponentsBuilder.replacePath("feedback-api/favourite-products/{id}")
                            .build(favouriteProduct.getId()))
                    .body(favouriteProduct));
  }

  @DeleteMapping("by-product-id/{productId:\\d+}")
  public Mono<ResponseEntity<Void>> removeProductFromFavourites(@PathVariable("productId") int productId){
    return this.productService.removeProductFromFavourites(productId)
            .then(Mono.just(ResponseEntity.noContent().build()));
  }
}
