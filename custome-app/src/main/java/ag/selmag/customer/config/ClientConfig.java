package ag.selmag.customer.config;

import ag.selmag.customer.client.WebClientFavouriteProductsClient;
import ag.selmag.customer.client.WebClientProductReviewsClient;
import ag.selmag.customer.client.WebClientProductsClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class ClientConfig {

  @Bean
  public WebClientProductsClient webClientProductsClient(
          @Value("${selmag.services.catalogue.uri:http://localhost:8081}") String baseUrl
  ) {
    return new WebClientProductsClient(WebClient.builder()
            .baseUrl(baseUrl)
            .build());
  }

  @Bean
  public WebClientFavouriteProductsClient webClientFavouriteProductsClient(
          @Value("${selmag.services.feedback.uri:http://localhost:8084}") String feedBackBaseUrl
  ) {
    return new WebClientFavouriteProductsClient(WebClient.builder()
            .baseUrl(feedBackBaseUrl)
            .build());
  }

  @Bean
  public WebClientProductReviewsClient webClientProductReviewsClient(
          @Value("${selmag.services.feedback.uri:http://localhost:8084}") String feedBackBaseUrl
  ) {
    return new WebClientProductReviewsClient(WebClient.builder()
            .baseUrl(feedBackBaseUrl)
            .build());
  }

}
