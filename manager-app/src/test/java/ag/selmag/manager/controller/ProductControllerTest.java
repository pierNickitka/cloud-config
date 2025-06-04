package ag.selmag.manager.controller;

import ag.selmag.manager.controller.client.BadRequestException;
import ag.selmag.manager.controller.client.ProductsRestClient;
import ag.selmag.manager.controller.payload.UpdateProductPayload;
import ag.selmag.manager.entity.Product;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.ui.ConcurrentModel;

import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles(value = {"standalone", "test"})
class ProductControllerTest {

  @InjectMocks
  ProductController controller;
  @Mock
  ProductsRestClient restClient;
  @Mock
  MessageSource messageSource;

  @Test
  void product_ProductExists_ReturnProduct(){
    //given
    var productId = 1;
    var product = new Product(1, "Товар", "Описание товара");

    doReturn(Optional.of(product)).when(this.restClient).findProduct(1);
    //when
    var res = this.controller.product(productId);
    //then
    assertEquals(product, res);

    verify(this.restClient).findProduct(productId);
    verifyNoMoreInteractions(this.restClient);
  }

  @Test
  void product_ProductDoesNotExist_ThrowsNoSuchElementException() {
    // given

    // when
    var exception = assertThrows(NoSuchElementException.class,
            () -> this.controller.product(1));
    // then
    assertEquals("catalogue.errors.product.not_found", exception.getMessage());

    verify(this.restClient).findProduct(1);
    verifyNoMoreInteractions(this.restClient);
  }

  @Test
  void getProduct_ReturnsProductPage(){
    //when
    String res = this.controller.getProduct();
    //then
    assertThat(res).isEqualTo("catalogue/products/product");
  }

  @Test
  void getProductEditPage_ReturnsProductEditPage(){
    //when
    String result = this.controller.getProductEditPage();
    //then
    assertThat(result).isEqualTo("catalogue/products/edit");
  }


  @Test
  void editProduct_RequestIsValid_ReturnsRedirectToProductPage(){
    //given
    var product = new Product(1,"Продукт", "Описание продукта");
    var editProduct = new UpdateProductPayload("Изменный продукт", "Опиисание изменного продукта");
    var model = new ConcurrentModel();
    var response = new MockHttpServletResponse();

    //when
    String result = this.controller.editProduct(product, editProduct, model,response);
    //then
    assertEquals("redirect:/catalogue/products/1", result);

    verify(this.restClient).updateProduct(1, "Изменный продукт", "Опиисание изменного продукта");
    verifyNoMoreInteractions(this.restClient);
  }

  @Test
  void editProduct_RequestIsInvalid_ReturnsEditProductPageWithExceptions(){
    //given
    var product = new Product(1,"Продукт", "Описание продукта");
    var editProduct = new UpdateProductPayload(null, "Описание изменного продукта");
    var model = new ConcurrentModel();
    var response = new MockHttpServletResponse();
    BadRequestException exception = new BadRequestException(List.of("Ошибка 1", "Ошибка 2"));

    doThrow(exception)
            .when(this.restClient).updateProduct(1, null, "Описание изменного продукта");
    //when
    String result = this.controller.editProduct(product, editProduct, model, response);
    //then

    assertEquals("catalogue/products/edit", result);
    assertEquals(editProduct, model.getAttribute("payload"));
    assertEquals(List.of("Ошибка 1","Ошибка 2"),model.getAttribute("errors"));
    assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());

    verify(this.restClient).updateProduct(1, null, "Описание изменного продукта");
  }


  @Test
  void deleteProduct_RequestIsValid_ReturnsRedirectToProductListPage(){
    var product = new Product(1,"Продукт", "Описание продукта");
    var model = new ConcurrentModel();
    var response = new MockHttpServletResponse();

    //when
    String result = this.controller.deleteProduct(product);
    //then
    assertThat(result).isEqualTo("redirect:/catalogue/products/list");

    verify(this.restClient).deleteProduct(1);
    verifyNoMoreInteractions(this.restClient);
  }

  @Test
  void handleNoSuchElementException_ReturnsPageNotFoundException(){
    var exception = new NoSuchElementException("error");
    var model = new ConcurrentModel();
    var response = new MockHttpServletResponse();
    var locale = new Locale("ru");

    doReturn("Ошибка").when(this.messageSource)
            .getMessage("error", new Object[0], "error", new Locale("ru"));
    //when
    String result = this.controller.handleNoSuchElementException(exception, model, response, locale);
    //then
    assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
    assertEquals("Ошибка", model.getAttribute("error"));
    assertEquals("errors/404",result);

    verify(this.messageSource).getMessage("error", new Object[0], "error", new Locale("ru"));
    verifyNoMoreInteractions(this.messageSource);
    verifyNoInteractions(this.restClient);
  }
}