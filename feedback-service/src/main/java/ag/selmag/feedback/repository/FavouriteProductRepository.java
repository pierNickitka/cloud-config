package ag.selmag.feedback.repository;

import ag.selmag.feedback.entity.FavouriteProduct;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface FavouriteProductRepository extends ReactiveCrudRepository<FavouriteProduct, UUID> {

  Mono<Void> deleteByProductId(int productId);

  Mono<FavouriteProduct> findByProductId(int productId);

}
