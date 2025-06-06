package com.example.patrioticstreaming.config;


import com.example.patrioticstreaming.web.client.OAuthHeadersProvider;
import jakarta.annotation.Priority;
import org.apache.hc.core5.http.HttpHeaders;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.client.AuthorizedClientServiceOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.web.SecurityFilterChain;

import java.util.Optional;

@Configuration
public class SecurityBeans {


  @Bean
  OAuthHeadersProvider oauthHttpHeadersProvider(
          ClientRegistrationRepository clientRegistrationRepository,
          OAuth2AuthorizedClientService authorizedClientService
  ) {
    return new OAuthHeadersProvider(new AuthorizedClientServiceOAuth2AuthorizedClientManager(
            clientRegistrationRepository, authorizedClientService));
  }

  @Bean
  @Priority(0)
  SecurityFilterChain apiSecurityFilterChain(HttpSecurity http) throws Exception {
    return http
            .securityMatcher(request -> Optional.ofNullable(request.getHeader(HttpHeaders.AUTHORIZATION))
                    .map(header -> header.startsWith("Bearer "))
                    .orElse(false))
            .oauth2ResourceServer(customizer -> customizer.jwt(Customizer.withDefaults()))
            .authorizeHttpRequests(registry -> registry.anyRequest().hasAuthority("SCOPE_metrics_server"))
            .sessionManagement(configurer -> configurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .csrf(CsrfConfigurer::disable)
            .build();
  }

  @Bean
  @Priority(1)
  SecurityFilterChain uiSecurityFilterChain(HttpSecurity http) throws Exception {
    return http
            .oauth2Client(Customizer.withDefaults())
            .oauth2Login(Customizer.withDefaults())
            .authorizeHttpRequests(registry -> registry.anyRequest().authenticated())
            .build();
  }

}
