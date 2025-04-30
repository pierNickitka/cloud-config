package ag.selmag.feedback.service;

import ag.selmag.feedback.entity.FavouriteProduct;
import ag.selmag.feedback.repository.FavouriteProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeafaultFavouriteProductService implements FavouriteProductService {

  private final FavouriteProductRepository favouriteProductRepository;

  @Override
  public Mono<FavouriteProduct> addProductToFavourites(int productId) {
    return this.favouriteProductRepository.save(new FavouriteProduct(UUID.randomUUID(), productId));
  }

  @Override
  public Mono<Void> removeProductFromFavourites(int productId) {
    return this.favouriteProductRepository.deleteByProductId(productId);
  }

  @Override
  public Mono<FavouriteProduct> findFavouriteProductByProduct(int productId) {
    return this.favouriteProductRepository.findByProductId(productId);
  }

  @Override
  public Flux<FavouriteProduct> findFavouriteProducts() {
    return this.favouriteProductRepository.findAll();
  }
}
