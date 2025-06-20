package ag.selmag.manager.config;

import ag.selmag.manager.controller.client.RestClientProductsRestClientImpl;
import ag.selmag.manager.security.OAuthClientHttpRequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.cloud.client.loadbalancer.LoadBalancerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.support.BasicAuthenticationInterceptor;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.web.client.RestClient;

@Configuration
public class ClientBeans {

  @Bean
  public RestClientProductsRestClientImpl productsRestClient(
          @Value("${selmag.services.catalogue.uri:http://localhost:8081}") String catalogueBaseUri,
          ClientRegistrationRepository clientRegistrationRepository,
          OAuth2AuthorizedClientRepository authorizedClientRepository,
          @Value("${selmag.services.catalogue.registration-id:keycloak}") String registrationId,
          LoadBalancerClient loadBalancerClient)
    {
    return new RestClientProductsRestClientImpl(RestClient.builder()
            .baseUrl(catalogueBaseUri)
            .requestInterceptor(new LoadBalancerInterceptor(loadBalancerClient))
            .requestInterceptor(
                    new OAuthClientHttpRequestInterceptor(new DefaultOAuth2AuthorizedClientManager
                                    (clientRegistrationRepository,authorizedClientRepository), registrationId))
            .build());
  }
}
