package ag.selmag.feedback.controller;

import ag.selmag.feedback.entity.FavouriteProduct;
import ag.selmag.feedback.service.FavouriteProductService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
class FavouriteProductsRestControllerTest {

  @Mock
  FavouriteProductService service;

  @InjectMocks
  FavouriteProductsRestController controller;

  @Test
  void findFavouriteProducts_ReturnsFavouriteProducts(){
    var token = Mono.just(new JwtAuthenticationToken(Jwt.withTokenValue("e30.e30")
            .headers(header -> header.put("foo","bar"))
            .claim("sub", "bd7779c2-cb05-11ee-b5f3-df46a1249898")
            .build()));

    doReturn(Flux.fromIterable(List.of(new FavouriteProduct(UUID.fromString("bd7779c2-cb05-11ee-b5f3-df46a1249898"),
            1, "bd7779c2-cb05-11ee-b5f3-df46a1249898"),new FavouriteProduct(UUID.fromString("cd7249c2-cb05-11ee-b5f3-df46a1249898"),
                    1, "bd7779c2-cb05-11ee-b5f3-df46a1249898"))))
            .when(this.service).findFavouriteProducts("bd7779c2-cb05-11ee-b5f3-df46a1249898");

    StepVerifier.create(this.controller.findFavouriteProducts(token))
            .expectNext(new FavouriteProduct(UUID.fromString("bd7779c2-cb05-11ee-b5f3-df46a1249898"),
                    1, "bd7779c2-cb05-11ee-b5f3-df46a1249898"),new FavouriteProduct(UUID.fromString("cd7249c2-cb05-11ee-b5f3-df46a1249898"),
                    1, "bd7779c2-cb05-11ee-b5f3-df46a1249898"))
            .verifyComplete();
  }

  @Test
  void findFavouriteProductByProductId_ReturnsFavouriteProduct(){
    var token = Mono.just(new JwtAuthenticationToken(Jwt.withTokenValue("e30.e30")
            .headers(header -> header.put("foo","bar"))
            .claim("sub", "bd7779c2-cb05-11ee-b5f3-df46a1249898")
            .build()));

    doReturn(Mono.just(new FavouriteProduct(UUID.fromString("bd7779c3-cb05-11ee-b5f3-df46a1249898"),
            1, "bd7779c3-cb05-11ee-b5f3-df46a1249898"))).when(this.service)
            .findFavouriteProductByProduct(1,"bd7779c2-cb05-11ee-b5f3-df46a1249898");

    StepVerifier.create(this.controller.findFavouriteProductByProductId(token,1 ))
            .expectNext(new FavouriteProduct(UUID.fromString("bd7779c3-cb05-11ee-b5f3-df46a1249898"),
                    1, "bd7779c3-cb05-11ee-b5f3-df46a1249898"))
            .verifyComplete();
  }
}