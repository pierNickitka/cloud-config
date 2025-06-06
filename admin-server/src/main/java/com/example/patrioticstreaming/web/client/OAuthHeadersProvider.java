package com.example.patrioticstreaming.web.client;

import de.codecentric.boot.admin.server.domain.entities.Instance;
import de.codecentric.boot.admin.server.web.client.HttpHeadersProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;



@RequiredArgsConstructor
public class OAuthHeadersProvider implements HttpHeadersProvider {

  private final OAuth2AuthorizedClientManager authorizedClientManager;

  @Override
  public HttpHeaders getHeaders(Instance instance) {
    OAuth2AuthorizedClient authorizedClient = this.authorizedClientManager
            .authorize(OAuth2AuthorizeRequest.withClientRegistrationId("keycloak")
            .principal("admin-service")
            .build());
    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.setBearerAuth(authorizedClient.getAccessToken().getTokenValue());
    return httpHeaders;
  }
}
