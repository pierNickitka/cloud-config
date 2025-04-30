package ag.selmag.feedback.repository;

import ag.selmag.feedback.entity.ProductReview;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProductReviewsRepository {

  Mono<ProductReview> save(ProductReview productReview);

  Flux<ProductReview> findAllByProductId(int productId);
}
