package ag.selmag.manager.controller;

import ag.selmag.manager.controller.client.BadRequestException;
import ag.selmag.manager.controller.client.ProductsRestClient;
import ag.selmag.manager.controller.payload.NewProductPayload;
import ag.selmag.manager.entity.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.ConcurrentModel;
import org.springframework.ui.Model;

import java.net.BindException;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("модульные тесты ProductsRestController")
class ProductsControllerTest {

  @Mock
  ProductsRestClient productsRestClient;

  @InjectMocks
  ProductsController productsController;

  @Test
  @DisplayName("createProduct создаст новый товар и перенаправит на страницу созданного товара")
  void createProduct_RequestIsValid_ReturnsRedirectionToProductPage(){
    //given
    var product = new NewProductPayload("новый товар", "новое описание товара");
    var model = new ConcurrentModel();

    doReturn(new Product(1,"новый товар", "новое описание товара"))
            .when(this.productsRestClient)
            .createProduct("новый товар", "новое описание товара");
    //when
    String result = this.productsController.createProduct(product, model);
    //then
    assertEquals("redirect:/catalogue/products/1", result);
    verify(this.productsRestClient).createProduct("новый товар", "новое описание товара");
    verifyNoMoreInteractions(this.productsRestClient);
  }

  @Test
  @DisplayName("createProduct вернет страницу создания товара с ошибками, если запрос не валиден")
  void createProduct_isInvalidRequest_ReturnsCreateProductPageWithErrors(){
    //given
    var payload = new NewProductPayload("   ", null);
    var model = new ConcurrentModel();

    doThrow(new BadRequestException(List.of("Ошибка 1", "Ошибка 2")))
            .when(this.productsRestClient)
            .createProduct("   ", null);
    //when
    String result = this.productsController.createProduct(payload, model);
    //then
    assertEquals("catalogue/products/new_product", result);
    assertEquals(payload, model.getAttribute("payload"));
    assertEquals(List.of("Ошибка 1", "Ошибка 2"), model.getAttribute("errors"));

    verify(this.productsRestClient).createProduct("   ", null);
    verifyNoMoreInteractions(this.productsRestClient);
  }

}