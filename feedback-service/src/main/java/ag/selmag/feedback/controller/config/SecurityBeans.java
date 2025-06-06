package ag.selmag.feedback.controller.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;

@Configuration
public class SecurityBeans {

  @Bean
  public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http){
    return http
            .csrf(ServerHttpSecurity.CsrfSpec::disable)
            .authorizeExchange(authorizeExchangeSpec -> authorizeExchangeSpec
                    .pathMatchers("/webjars/**", "/v3/api-docs/**", "/swagger-ui.html", "/swagger-ui/**")
                      .permitAll()
                    .anyExchange().authenticated())
            .securityContextRepository(NoOpServerSecurityContextRepository.getInstance())
            .oauth2ResourceServer(customizer -> customizer.jwt(Customizer.withDefaults()))
            .build();
  }
}
