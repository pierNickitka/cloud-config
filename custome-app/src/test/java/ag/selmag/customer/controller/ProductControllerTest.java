package ag.selmag.customer.controller;

import ag.selmag.customer.client.FavouriteProductsClient;
import ag.selmag.customer.client.ProductReviewsClient;
import ag.selmag.customer.client.ProductsClient;
import ag.selmag.customer.client.exception.ClientBadRequestException;
import ag.selmag.customer.controller.payload.NewProductReviewPayload;
import ag.selmag.customer.entity.FavouriteProduct;
import ag.selmag.customer.entity.Product;
import ag.selmag.customer.entity.ProductReview;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.mock.http.server.reactive.MockServerHttpResponse;
import org.springframework.ui.ConcurrentModel;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductControllerTest {

  @Mock
  ProductsClient productsClient;

  @Mock
  FavouriteProductsClient favouriteProductClient;

  @Mock
  ProductReviewsClient productReviewsClient;

  @InjectMocks
  ProductController controller;

  @Test
  void findProduct_DataIsExists_ReturnsProduct(){
    //given
    Product product = new Product(1, "Товар", "Описание товара");
    doReturn(Mono.just(new Product(1, "Товар", "Описание товара")))
            .when(this.productsClient).findProduct(1);
    //when
    StepVerifier.create(this.controller.findProduct(1))
            //then
            .expectNext(product)
            .expectComplete()
            .verify();

    verify(this.productsClient).findProduct(1);
    verifyNoInteractions(favouriteProductClient, productReviewsClient);
  }

  @Test
  void findProduct_DataDoesNotExists_ReturnsProduct(){
    doReturn(Mono.empty()).when(this.productsClient).findProduct(1);

    StepVerifier.create(this.controller.findProduct(1))
            .expectErrorMatches(exception -> exception instanceof NoSuchElementException e &&
                    e.getMessage().equals("customer.products.error.not_found"))
            .verify();

    verify(this.productsClient).findProduct(1);
    verifyNoMoreInteractions(this.productsClient);
    verifyNoInteractions(this.favouriteProductClient, this.productReviewsClient);
  }

  @Test
  void getProductPage_ReturnsProductPage(){
    var model = new ConcurrentModel();
    var productReviews = List.of(
            new ProductReview(UUID.fromString("6a8512d8-cbaa-11ee-b986-376cc5867cf5"), 1, 5, "На пятёрочку"),
            new ProductReview(UUID.fromString("849c3fac-cbaa-11ee-af68-737c6d37214a"), 1, 4, "Могло быть и лучше"));

    doReturn(Flux.fromIterable(productReviews))
            .when(this.productReviewsClient).findProductReviewsByProductId(1);
    doReturn(Mono.just(new FavouriteProduct(UUID.randomUUID(), 1)))
            .when(this.favouriteProductClient).findFavouriteProductByProductId(1);

    StepVerifier.create(this.controller.getProductPage(1,model))
            .expectNext("customer/products/product")
            .expectComplete()
            .verify();

    assertEquals(true, model.getAttribute("inFavourite"));
    assertEquals(productReviews, model.getAttribute("reviews"));
  }

  @Test
  void addProductToFavourites_DataIsExists_ReturnsRedirectToCustomersProduct(){
    var product = new Product(1, "Товар", "Описание товара");

    doReturn(Mono.just(new FavouriteProduct(UUID.fromString("6a8512d8-cbaa-11ee-b986-376cc5867cf5"), 1)))
            .when(this.favouriteProductClient).addProductToFavourites(1);

    StepVerifier.create(this.controller.addProductToFavourites(Mono.just(product)))
            //then
            .expectNext("redirect:/customer/products/1")
            .verifyComplete();

    verify(this.favouriteProductClient).addProductToFavourites(1);
    verifyNoMoreInteractions(this.favouriteProductClient);
    verifyNoMoreInteractions(productsClient, productReviewsClient);
  }

  @Test
  void addProductToFavourites_DataDoesNotExists_ReturnsRedirectToCustomersProduct(){
    var exception = new ClientBadRequestException("Какая то ошибка",null , List.of("Ошибка"));
    doReturn(Mono.error(exception))
            .when(this.favouriteProductClient).addProductToFavourites(1);

    StepVerifier.create(this.controller.addProductToFavourites(Mono.just(new Product(1,"Товар", "Описание"))))
            .expectNext("redirect:/customer/products/1")
            .verifyComplete();
    verify(this.favouriteProductClient).addProductToFavourites(1);
    verifyNoMoreInteractions(this.favouriteProductClient);
    verifyNoInteractions(this.productReviewsClient, this.productsClient);
  }








  @Test
  void removeProductFromFavourites_ReturnsRedirectToProductPage(){
    //given
    Mono<Product> product = Mono.just(new Product(1,"Товар","Описание товара"));
    doReturn(Mono.empty()).when(this.favouriteProductClient).removeProductFromFavourites(1);
    //when
    StepVerifier.create(this.controller.removeProductFromFavourites(product))
            .expectNext("redirect:/customer/products/1")
            .expectComplete()
            .verify();
    //then
    verify(this.favouriteProductClient).removeProductFromFavourites(1);
    verifyNoMoreInteractions(this.favouriteProductClient);
    verifyNoInteractions(this.productsClient, this.productReviewsClient);
  }

  @Test
  void createReview_RequestIsValid_ReturnsRedirectProductPage(){
    var productReviewPayload = new NewProductReviewPayload(5,"Отличный товар");
    var model = new ConcurrentModel();
    var response = new MockServerHttpResponse();

    doReturn(Mono.just(new ProductReview(UUID.
            fromString("6a8512d8-cbaa-11ee-b986-376cc5867cf5"),1,5,"Отличный товар")))
            .when(this.productReviewsClient).createProductReview(1, 5, "Отличный товар");

    StepVerifier.create(this.controller.createReview(1, productReviewPayload, model, response))
            .expectNext("redirect:/customer/products/1")
            .expectComplete()
            .verify();
    verify(this.productReviewsClient).createProductReview(1, 5, "Отличный товар");
    verifyNoMoreInteractions(this.productReviewsClient);
    verifyNoInteractions(this.productsClient, this.favouriteProductClient);
  }

  @Test
  void createReview_RequestIsInvalid_ReturnsToProductPageWithExceptions(){
    var productReviewPayload = new NewProductReviewPayload(null,"Так се");
    var model = new ConcurrentModel();
    var exception = new ClientBadRequestException("Какая то ошибка", null, List.of("Ошибка"));
    var response = new MockServerHttpResponse();

    doReturn(Mono.error(exception))
            .when(this.productReviewsClient).createProductReview(1,null, "Так се");

    doReturn(Mono.just(new FavouriteProduct(UUID.fromString("6a8512d8-cbaa-11ee-b986-376cc5867cf5"), 1)))
            .when(this.favouriteProductClient).findFavouriteProductByProductId(1);

    StepVerifier.create(this.controller.createReview(1, productReviewPayload, model,response))
            .expectNext("customer/products/product")
            .expectComplete()
            .verify();

    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertEquals(productReviewPayload,model.getAttribute("payload"));
    assertEquals(exception.getErrors(),model.getAttribute("errors"));
    assertEquals(true, model.getAttribute("inFavourite"));

    verify(this.productReviewsClient).createProductReview(1,null, "Так се");
    verifyNoMoreInteractions(this.productReviewsClient);
    verifyNoInteractions(this.productsClient);
  }

  @Test
  void handlerNoSuchElementException_ReturnsPageErrors404(){
    var exception = new NoSuchElementException("Ошибка");
    var model = new ConcurrentModel();

    String result = this.controller.handlerNoSuchElementException(exception, model);

    assertEquals("errors/404",result);
    assertEquals("Ошибка", model.getAttribute("error"));
  }


}