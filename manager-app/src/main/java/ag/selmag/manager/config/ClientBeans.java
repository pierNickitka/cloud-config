package ag.selmag.manager.config;

import ag.selmag.manager.controller.client.RestClientProductsRestClientImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.support.BasicAuthenticationInterceptor;
import org.springframework.web.client.RestClient;

@Configuration
public class ClientBeans {

  @Bean
  public RestClientProductsRestClientImpl productsRestClient(
          @Value("${selmag.services.uri:http://localhost:8081}") String catalogueBaseUri,
          @Value("${selmag.services.username:}") String catalogueUsername,
          @Value("${selmag.services.password:}") String cataloguePassword) {
    return new RestClientProductsRestClientImpl(RestClient.builder()
            .baseUrl(catalogueBaseUri)
            .requestInterceptor(new BasicAuthenticationInterceptor(catalogueUsername,cataloguePassword))
            .build());
  }
}
