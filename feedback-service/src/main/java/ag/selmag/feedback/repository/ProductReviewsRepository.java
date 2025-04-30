package ag.selmag.feedback.repository;

import ag.selmag.feedback.entity.ProductReview;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface ProductReviewsRepository extends ReactiveCrudRepository<ProductReview, UUID> {

  Flux<ProductReview> findAllByProductId(int productId);
}
