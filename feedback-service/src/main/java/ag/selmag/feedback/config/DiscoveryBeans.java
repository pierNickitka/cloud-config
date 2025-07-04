package ag.selmag.feedback.config;

import org.apache.http.HttpHeaders;
import org.springframework.cloud.netflix.eureka.RestTemplateTimeoutProperties;
import org.springframework.cloud.netflix.eureka.http.DefaultEurekaClientHttpRequestFactorySupplier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.AuthorizedClientServiceReactiveOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import reactor.core.publisher.Mono;

import java.util.List;

@Configuration
public class DiscoveryBeans {

  @Bean
  public DefaultEurekaClientHttpRequestFactorySupplier defaultEurekaClientHttpRequestFactorySupplier(
          RestTemplateTimeoutProperties restTemplateTimeoutProperties,
          ReactiveClientRegistrationRepository clientRegistrationRepository,
          ReactiveOAuth2AuthorizedClientService authorizedClientService
  ){
    AuthorizedClientServiceReactiveOAuth2AuthorizedClientManager authorizedClientManager =
            new AuthorizedClientServiceReactiveOAuth2AuthorizedClientManager(clientRegistrationRepository,
                    authorizedClientService);
    return new DefaultEurekaClientHttpRequestFactorySupplier(restTemplateTimeoutProperties,
            List.of((httpRequest, entityDetails, httpContext) -> {
              if(!httpRequest.containsHeader(HttpHeaders.AUTHORIZATION)){
                OAuth2AuthorizedClient authorizedClient = authorizedClientManager
                        .authorize(OAuth2AuthorizeRequest
                                .withClientRegistrationId("discovery")
                                .principal("feedback-service")
                                .build())
                        .block();
                httpRequest.setHeader(HttpHeaders.AUTHORIZATION,
                        "Bearer %s".formatted(authorizedClient.getAccessToken().getTokenValue()));
              }
            }));
  }
}
