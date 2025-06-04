package ag.selmag.manager.controller;

import ag.selmag.manager.controller.client.BadRequestException;
import ag.selmag.manager.controller.payload.UpdateProductPayload;
import ag.selmag.manager.entity.Product;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jackson.JsonMixinModuleEntries;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles(value = {"standalone","test"})
@WireMockTest(httpPort = 54321)
class ProductControllerIT {

  @Autowired
  MockMvc mockMvc;
  @Autowired
  private JsonMixinModuleEntries jsonMixinModuleEntries;

  @Test
  void getProduct_RequestIsValid_ReturnsProduct() throws Exception{
    //given
    MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get("/catalogue/products/1")
            .with(user("j.dewar").roles("MANAGER"));

    WireMock.stubFor(WireMock.get(WireMock.urlPathMatching("/catalogue-api/products/1"))
            .willReturn(WireMock.ok(
                    """
                          {"id": 1, "title": "Товар", "details": "Описание товара"}
                          """).withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)));
    //when
    this.mockMvc.perform(request)
            .andDo(print())
            //then
            .andExpectAll(
                    status().isOk(),
                    view().name("catalogue/products/product"),
                    model().attribute("product", new Product(1, "Товар", "Описание товара")));
    WireMock.verify(WireMock.getRequestedFor(WireMock.urlPathMatching("/catalogue-api/products/1")));
  }

  @Test
  void getProduct_RequestIsInvalid_ReturnsError404Page() throws Exception{
    //given
    MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get("/catalogue/products/1")
            .with(user("j.dewar").roles("MANAGER"))
            .locale(new Locale("ru", "RU"));

    WireMock.stubFor(WireMock.get(WireMock.urlPathMatching("/catalogue-api/products/1"))
            .willReturn(WireMock.notFound()));
    //when
    this.mockMvc.perform(request)
            .andDo(print())
            //then
            .andExpectAll(
                    status().isNotFound(),
                    view().name("errors/404"),
                    model().attribute("error", "Товар не найден"));
  }


  @Test
  void getProductEditPage_RequestIsValid_ReturnsProductEditPage() throws Exception{
    //given
    MockHttpServletRequestBuilder request = MockMvcRequestBuilders
            .get("/catalogue/products/1/edit")
            .with(user("j.dewar").roles("MANAGER"));
    WireMock.stubFor(WireMock.get(WireMock.urlPathMatching("/catalogue-api/products/1"))
            .willReturn(WireMock.okJson(
                    """
                          {"id": 1, "title": "Товар", "details": "Описание товара"}
                          """)));
    //when
    this.mockMvc.perform(request)
            .andDo(print())
            //then
            .andExpectAll(
                    status().isOk(),
                    view().name("catalogue/products/edit"),
                    model().attribute("product", new Product(1,"Товар","Описание товара")));
  }


  @Test
  void getProductEditPage_RequestIsInvalid_ReturnsError404Page() throws Exception{
    MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get("/catalogue/products/1/edit")
            .with(user("j.dewar").roles("MANAGER"));

    WireMock.stubFor(WireMock.get(WireMock.urlPathMatching("/catalogue-api/products/1"))
            .willReturn(WireMock.notFound()));

    this.mockMvc.perform(request)
            .andDo(print())
            .andExpectAll(
                    status().isNotFound(),
                    view().name("errors/404"),
                    model().attribute("error", "Товар не найден"));
  }


  @Test
  void editProduct_RequestIsValid_ReturnsRedirectToProductPage() throws Exception{
    MockHttpServletRequestBuilder request = MockMvcRequestBuilders
            .post("/catalogue/products/1/edit")
            .with(user("j.dewar").roles("MANAGER"))
            .param("title", "Товар изм")
            .param("details", "Описание товара изм")
            .contentType(MediaType.APPLICATION_FORM_URLENCODED);

    WireMock.stubFor(WireMock.get("/catalogue-api/products/1")
            .willReturn(WireMock.okJson(
                    """
                            {
                            "id": 1,
                            "title": "Товар",
                            "details": "Описание товара"
                            }
                            """)));

    WireMock.stubFor(WireMock.patch(WireMock.urlPathMatching("/catalogue-api/products/1"))
            .withRequestBody(WireMock.equalToJson(
                    """
                            {"title": "Товар изм", "details": "Описание товара изм"}
                            """))
            .willReturn(WireMock.noContent()));

    this.mockMvc.perform(request)
            .andDo(print())
            .andExpectAll(
                    status().is3xxRedirection(),
                    redirectedUrl("/catalogue/products/1"));

    WireMock.verify(WireMock.patchRequestedFor(WireMock.urlPathMatching("/catalogue-api/products/1"))
            .withRequestBody(WireMock.equalToJson(
                    """
                           {"title": "Товар изм", "details": "Описание товара изм"}
                           """)));
  }

  @Test
  void editProduct_RequestIsInvalid_ReturnsEditPageWithValidationErrors() throws Exception{
    MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post("/catalogue/products/1/edit")
            .with(user("j.dewar").roles("MANAGER"))
            .param("details", "  ")
            .with(csrf())
            .contentType(MediaType.APPLICATION_FORM_URLENCODED);

    WireMock.stubFor(WireMock.get("/catalogue-api/products/1")
            .willReturn(WireMock.okJson(
                    """
                          {
                            "id": 1,
                            "title": "Товар",
                            "details": "Описание товара"
                          }
                          """)));

    WireMock.stubFor(WireMock.patch("/catalogue-api/products/1")
            .withRequestBody(WireMock.equalToJson(
                    """
                          {"title": null, "details":  "  "}
                          """))
            .willReturn(WireMock.badRequest()
                    .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .withBody("""
                            {"errors": ["Ошибка 1","Ошибка 2"]}
                            """)));

    this.mockMvc.perform(request)
            .andDo(print())
            .andExpectAll(
                    status().isBadRequest(),
                    view().name("catalogue/products/edit"),
                    model().attribute("payload", new UpdateProductPayload(null, "  ")),
                    model().attribute("errors", List.of("Ошибка 1","Ошибка 2")));
  }

  @Test
  void deleteProduct_RequestIsValid_ReturnsRedirectToProductPage() throws Exception{
    MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post("/catalogue/products/1/delete")
            .with(user("j.dewar").roles("MANAGER"));

    WireMock.stubFor(WireMock.get("/catalogue-api/products/1")
            .willReturn(WireMock.okJson(
                    """
                          {
                            "id": 1,
                            "title": "Товар",
                            "details": "Описание товара"
                          }
                          """)));

    WireMock.stubFor(WireMock.delete("/catalogue-api/products/1")
            .willReturn(WireMock.noContent()));

    this.mockMvc.perform(request)
            .andDo(print())
            .andExpectAll(
                    status().is3xxRedirection(),
                    redirectedUrl("/catalogue/products/list"));
  }

  @Test
  void deleteProduct_RequestIsInvalid_ReturnsRedirectToProductPage() throws Exception{
    MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post("/catalogue/products/1/delete")
            .with(user("j.dewar").roles("MANAGER"));

    WireMock.stubFor(WireMock.get("/catalogue-api/products/1")
            .willReturn(WireMock.notFound()));


    this.mockMvc.perform(request)
            .andDo(print())
            .andExpectAll(
                    status().isNotFound(),
                    view().name("errors/404"),
                    model().attribute("error", "Товар не найден"));
  }
}
