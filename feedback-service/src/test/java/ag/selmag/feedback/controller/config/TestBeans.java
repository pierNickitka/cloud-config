package ag.selmag.feedback.controller.config;

import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.testcontainers.containers.MongoDBContainer;

import static org.mockito.Mockito.mock;

@Configuration
public class TestBeans {

  @Bean(initMethod = "start", destroyMethod = "stop")
  @ServiceConnection
  public MongoDBContainer mongoDBContainer() {
    return new MongoDBContainer("mongo:7");
  }

  @Bean
  public ReactiveJwtDecoder jwtDecoder() {
    return mock(ReactiveJwtDecoder.class);
  }
}
