package ag.selmag.catalogue.controller;

import ag.selmag.catalogue.controller.payload.NewProductPayload;
import ag.selmag.catalogue.entity.Product;
import ag.selmag.catalogue.service.ProductService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.validation.MapBindingResult;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
@ActiveProfiles(value = {"standalone","test"})
class ProductsRestControllerTest {

  @Mock
  ProductService productService;
  @InjectMocks
  ProductsRestController productsRestController;

  @Test
  void findProducts_ReturnsListOfProducts(){
    //given
    var filter = "товар";
    Iterable<Product> products = IntStream.range(1, 4)
            .mapToObj(i -> new Product(i, "Товар №" + i, "Описание товара №" + i))
            .toList();
    Mockito.doReturn(products)
            .when(this.productService).findAllProducts("%" + "товар" + "%");
    //when
    Iterable<Product> result = productsRestController.findProducts(filter);
    //then
    assertEquals(products, result);

    verify(this.productService,times(1)).findAllProducts("%" + filter + "%");
    verifyNoMoreInteractions(this.productService);
  }

  @Test
  void createProduct_DataIsValid_ReturnsNewProduct() throws BindException {
    var productPayload = new NewProductPayload("Новый товар","Описание нового товара");
    var bindingResult = new MapBindingResult(Map.of(),"payload");
    var uriComponentsBuilder = UriComponentsBuilder.fromUriString("http://localhost");
    var returnProduct = new Product(1, "Новый товар", "Описание нового товара");

    doReturn(returnProduct)
            .when(this.productService).createProduct(productPayload.title(), productPayload.details());

    ResponseEntity<?> result = this.productsRestController.createProduct(productPayload, bindingResult, uriComponentsBuilder);

    assertEquals(HttpStatus.CREATED, result.getStatusCode());
    assertEquals(returnProduct, result.getBody());
    assertEquals("http://localhost/catalogue-api/products/1",
            Objects.requireNonNull(result.getHeaders().get(HttpHeaders.LOCATION)).get(0));
    assertEquals(URI.create("http://localhost/catalogue-api/products/1"),
            result.getHeaders().getLocation());

    verify(this.productService).createProduct("Новый товар", "Описание нового товара");
    verifyNoMoreInteractions(this.productService);
  }

  @Test
  void createProduct_DataIsInvalid_ReturnsBadRequest() throws BindException {
    var incorrectPayload = new NewProductPayload("  ","Описание нового товара");
    var bindingResult = new MapBindingResult(Map.of(),"payload");
    bindingResult.addError(new FieldError("payload", "title", "error"));
    var uriComponentsBuilder = UriComponentsBuilder.fromUriString("http://localhost");

    BindException exception = assertThrows(BindException.class, () -> this.productsRestController
            .createProduct(incorrectPayload, bindingResult, uriComponentsBuilder));

    assertEquals(List.of(new FieldError("payload", "title", "error")),
            exception.getAllErrors());
    verifyNoInteractions(this.productService);
  }

}