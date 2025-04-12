package ag.selmag.manager.controller.client;

import ag.selmag.manager.controller.payload.NewProductPayload;
import ag.selmag.manager.controller.payload.UpdateProductPayload;
import ag.selmag.manager.entity.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.parsing.Problem;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

@RequiredArgsConstructor
public class RestClientProductsRestClientImpl implements ProductsRestClient {
  private final RestClient restClient;

  private final static ParameterizedTypeReference<List<Product>> PRODUCTS_TYPE_REFERENCE =
          new ParameterizedTypeReference<>(){};

  @Override
  public List<Product> findAllProducts(String filter) {
    return this.restClient
            .get()
            .uri("/catalogue-api/products?filter={filter}",filter)
            .retrieve()
            .body(PRODUCTS_TYPE_REFERENCE);
  }

  @Override
  public Product createProduct(String title, String details) {
    try{
      return this.restClient
              .post()
              .uri("/catalogue-api/products")
              .contentType(MediaType.APPLICATION_JSON)
              .body(new NewProductPayload(title,details))
              .retrieve()
              .body(Product.class);
    } catch (HttpClientErrorException.BadRequest exception) {
      ProblemDetail problemDetail = exception.getResponseBodyAs(ProblemDetail.class);
      throw new BadRequestException((List<String>) problemDetail.getProperties().get("errors"));
    }

  }

  @Override
  public Optional<Product> findProduct(int productId) {
    try {
      return Optional.ofNullable(this.restClient.get()
              .uri("/catalogue-api/products/{productId}", Map.of("productId", productId))
              .retrieve()
              .body(Product.class));
    } catch (HttpClientErrorException.NotFound exception){
      return Optional.empty();
    }
  }

  @Override
  public void updateProduct(int id, String title, String details) {
    try{
      this.restClient
              .patch()
              .uri("/catalogue-api/products/{productId}", Map.of("productId", id))
              .contentType(MediaType.APPLICATION_JSON)
              .body(new UpdateProductPayload(title,details))
              .retrieve()
              .toBodilessEntity();
    } catch (HttpClientErrorException.BadRequest exception) {
      ProblemDetail problemDetail = exception.getResponseBodyAs(ProblemDetail.class);
      throw new BadRequestException((List<String>) problemDetail.getProperties().get("errors"));
    }
  }

  @Override
  public void deleteProduct(int id) {
    try {
      this.restClient.delete()
              .uri("/catalogue-api/products/{productId}", Map.of("productId", id))
              .retrieve()
              .toBodilessEntity();
    } catch (HttpClientErrorException.NotFound exception){
      throw new NoSuchElementException(exception);
    }
  }
}
