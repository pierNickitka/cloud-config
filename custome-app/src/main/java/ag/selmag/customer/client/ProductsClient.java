package ag.selmag.customer.client;

import ag.selmag.customer.entity.Product;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProductsClient {
  Flux<Product> findAllProducts(String filter);

  Mono<Product> findProduct(int id);
}
