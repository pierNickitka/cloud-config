package ag.selmag.feedback.controller;


import ag.selmag.feedback.controller.payload.NewFavouriteProductPayload;
import ag.selmag.feedback.entity.FavouriteProduct;
import ag.selmag.feedback.service.FavouriteProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
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
  public Flux<FavouriteProduct> findFavouriteProducts(Mono<JwtAuthenticationToken> tokenMono) {
    return tokenMono.flatMapMany(token -> this.productService.findFavouriteProducts(token.getToken().getSubject()));
  }

  @GetMapping("by-product-id/{productId:\\d+}")
  public Mono<FavouriteProduct> findFavouriteProductByProductId(
          Mono<JwtAuthenticationToken> tokenMono,@PathVariable("productId") int id) {
    return tokenMono.flatMap(token  -> this.productService
            .findFavouriteProductByProduct(id, token.getToken().getSubject()));
  }

  @PostMapping
  public Mono<ResponseEntity<FavouriteProduct>> addProductToFavourite(Mono<JwtAuthenticationToken> tokenMono,
                    @Valid @RequestBody Mono<NewFavouriteProductPayload> payloadMono,
                    UriComponentsBuilder uriComponentsBuilder) {
    return Mono.zip(tokenMono, payloadMono)
            .flatMap(tuple2 -> this.productService
                    .addProductToFavourites(tuple2.getT2().productId(), tuple2.getT1().getToken().getSubject()))
            .map(favouriteProduct -> ResponseEntity
                    .created(uriComponentsBuilder.replacePath("feedback-api/favourite-products/{id}")
                            .build(favouriteProduct.getId()))
                    .body(favouriteProduct));
  }

  @DeleteMapping("by-product-id/{productId:\\d+}")
  public Mono<ResponseEntity<Void>> removeProductFromFavourites(Mono<JwtAuthenticationToken> tokenMono,
                                                                @PathVariable("productId") int productId){
    return tokenMono.flatMap(token -> this.productService.removeProductFromFavourites(productId,
                    token.getToken().getSubject()))
            .then(Mono.just(ResponseEntity.noContent().build()));
  }

}
