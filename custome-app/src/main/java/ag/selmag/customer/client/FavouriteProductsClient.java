package ag.selmag.customer.client;

import ag.selmag.customer.entity.FavouriteProduct;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Optional;

public interface FavouriteProductsClient {


  Flux<FavouriteProduct> findFavouriteProducts();

  Mono<FavouriteProduct> findFavouriteProductByProductId(int id);

  Mono<FavouriteProduct> addProductToFavourites(Integer productId);

  Mono<Void> removeProductFromFavourites(Integer productId);
}
