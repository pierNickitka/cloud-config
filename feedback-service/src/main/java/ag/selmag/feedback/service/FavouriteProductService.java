package ag.selmag.feedback.service;

import ag.selmag.feedback.entity.FavouriteProduct;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface FavouriteProductService {

  Mono<FavouriteProduct> addProductToFavourites(int productId);

  Mono<Void> removeProductFromFavourites(int productId);

  Mono<FavouriteProduct> findFavouriteProductByProduct(int productId);

  Flux<FavouriteProduct> findFavouriteProducts();
}
