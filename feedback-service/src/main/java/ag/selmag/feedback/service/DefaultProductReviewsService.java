package ag.selmag.feedback.service;

import ag.selmag.feedback.entity.ProductReview;
import ag.selmag.feedback.repository.ProductReviewsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DefaultProductReviewsService implements ProductReviewsService {

  private final ProductReviewsRepository productReviewsRepository;

  @Override
  public Mono<ProductReview> createProductReview(int productId, int rating, String review, String userId) {
    return this.productReviewsRepository.save(new ProductReview(UUID.randomUUID(), productId, rating, review, userId));
  }

  @Override
  public Flux<ProductReview> findProductReviewsByProduct(int productId) {
    return this.productReviewsRepository.findAllByProductId(productId);
  }
}
