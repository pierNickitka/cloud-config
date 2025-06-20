package ag.selmag.customer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
@EnableDiscoveryClient
public class CustomerApplication {
  public static void main(String[] args) {
    SpringApplication.run(CustomerApplication.class, args);
  }
}
