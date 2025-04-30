package ag.selmag.feedback.controller;

import ag.selmag.feedback.controller.payload.NewProductReviewPayload;
import ag.selmag.feedback.entity.ProductReview;
import ag.selmag.feedback.service.ProductReviewsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("feedback-api/product-reviews")
@RequiredArgsConstructor
public class ProductReviewsRestController {

  private final ProductReviewsService productReviewsService;

  @GetMapping("by-product-id/{productId:\\d+}")
  public Flux<ProductReview> findProductReviewsByProductId(@PathVariable("productId") int productId) {
    return this.productReviewsService.findProductReviewsByProduct(productId);
  }

  @PostMapping
  public Mono<ResponseEntity<ProductReview>> createProductReview(@Valid @RequestBody Mono<NewProductReviewPayload> payloadMono,
                                                                 UriComponentsBuilder uriBuilder) {
    return payloadMono
            .flatMap(productReview -> this.productReviewsService.createProductReview
                    (productReview.productId(), productReview.rating(), productReview.reviews()))
            .map(productReview -> ResponseEntity
                    .created(uriBuilder.replacePath("/feedback-api/product-reviews/{id}")
                            .build(productReview.getId()))
                    .body(productReview));
  }



}
