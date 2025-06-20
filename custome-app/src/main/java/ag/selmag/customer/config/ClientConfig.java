package ag.selmag.customer.config;

import ag.selmag.customer.client.WebClientFavouriteProductsClient;
import ag.selmag.customer.client.WebClientProductReviewsClient;
import ag.selmag.customer.client.WebClientProductsClient;
import io.micrometer.observation.ObservationRegistry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.client.loadbalancer.reactive.ReactorLoadBalancerExchangeFilterFunction;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.security.oauth2.client.web.server.ServerOAuth2AuthorizedClientRepository;
import org.springframework.web.reactive.function.client.DefaultClientRequestObservationConvention;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class ClientConfig {

  @Bean
  @Scope("prototype")
  @LoadBalanced
  public WebClient.Builder selmaWebClientBuilder(
          ReactiveClientRegistrationRepository clientRegistrationRepository,
          ServerOAuth2AuthorizedClientRepository authorizedClientRepository,
          ObservationRegistry observationRegistry
  ){
    ServerOAuth2AuthorizedClientExchangeFilterFunction filter = new ServerOAuth2AuthorizedClientExchangeFilterFunction(clientRegistrationRepository,
            authorizedClientRepository);
    filter.setDefaultClientRegistrationId("keycloak");
    return WebClient.builder()
            .observationRegistry(observationRegistry)
            .observationConvention(new DefaultClientRequestObservationConvention())
            .filter(filter);
  }

  @Bean
  public WebClientProductsClient webClientProductsClient(
          @Value("${selmag.services.catalogue.uri:http://localhost:8081}") String baseUrl,
          WebClient.Builder selmaWebClientBuilder
  ) {
    return new WebClientProductsClient(selmaWebClientBuilder
            .baseUrl(baseUrl)
            .build());
  }

  @Bean
  public WebClientFavouriteProductsClient webClientFavouriteProductsClient(
          @Value("${selmag.services.feedback.uri:http://localhost:8084}") String feedBackBaseUrl,
          WebClient.Builder selmaWebClientBuilder
  ) {
    return new WebClientFavouriteProductsClient(selmaWebClientBuilder
            .baseUrl(feedBackBaseUrl)
            .build());
  }

  @Bean
  public WebClientProductReviewsClient webClientProductReviewsClient(
          @Value("${selmag.services.feedback.uri:http://localhost:8084}") String feedBackBaseUrl,
          WebClient.Builder selmaWebClientBuilder
  ) {
    return new WebClientProductReviewsClient(selmaWebClientBuilder
            .baseUrl(feedBackBaseUrl)
            .build());
  }

}
