package ag.selmag.manager.controller;

import ag.selmag.manager.controller.payload.NewProductPayload;
import ag.selmag.manager.entity.Product;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(printOnlyOnFailure = false)
@ActiveProfiles(value = {"standalone", "test"})
@WireMockTest(httpPort = 54321)
class ProductsControllerIT {
  @Autowired
  MockMvc mockMvc;

  @Test
  void getNewProductPage_ReturnsProductPage() throws Exception {
    //GIVEN
    MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get("/catalogue/products/create")
            .with(user("j.dewar").roles("MANAGER"));
    //WHEN
    this.mockMvc.perform(request)
            .andDo(print())
            //THEN
            .andExpectAll(
                    status().isOk(),
                    view().name("catalogue/products/new_product")
            );
  }

  @Test
  void getProductsList_returnsProductListPage() throws Exception {
    var requestBuilder = MockMvcRequestBuilders.get("/catalogue/products/list")
            .queryParam("filter", "товар")
            .with(user("j.dewar").roles("MANAGER"));

    WireMock.stubFor(WireMock.get(WireMock.urlPathMatching("/catalogue-api/products"))
            .withQueryParam("filter", WireMock.equalTo("товар"))
            .willReturn(WireMock.ok(
                    """
                            [
                            {"id": 1, "title": "Товар №1", "details": "Описание товара №1"},
                            {"id": 2, "title": "Товар №2", "details": "Описание товара №2"}
                            ]
                            """).withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)));
    //when
    this.mockMvc.perform(requestBuilder)
            //given
            .andDo(print())
            .andExpectAll(
                    status().isOk(),
                    model().attribute("filter", "товар"),
                    model().attribute("products",
                            List.of(new Product(1, "Товар №1", "Описание товара №1"),
                                    new Product(2, "Товар №2", "Описание товара №2"))),
                    view().name("catalogue/products/list"));

    WireMock.verify(WireMock.getRequestedFor(WireMock.urlPathMatching("/catalogue-api/products"))
            .withQueryParam("filter", WireMock.equalTo("товар")));
  }

  @Test
  void createProduct_RequestIsValid_ReturnsRedirectToProductPage() throws Exception{
    //given
    MockHttpServletRequestBuilder request = MockMvcRequestBuilders
            .post("/catalogue/products/create")
            .param("title", "Товар")
            .param("details", "Описание товара")
            .with(user("j.dewar").roles("MANAGER"))
            .with(csrf())
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED);

    WireMock.stubFor(WireMock.post(WireMock.urlPathMatching("/catalogue-api/products"))
            .withRequestBody(WireMock.equalToJson(
                    """
                           {"title": "Товар", "details": "Описание товара"}
                           """))
            .willReturn(WireMock.created()
                    .withHeader(HttpHeaders.LOCATION, "/catalogue-api/products/1")
                    .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .withBody("""
                              {"id": 1, "title": "Товар", "details": "Описание товара"}
                              """)));
    //when
    this.mockMvc.perform(request)
            //then
            .andDo(print())
            .andExpectAll(
                    status().is3xxRedirection(),
                    header().string(HttpHeaders.LOCATION, "/catalogue/products/1"),
                    redirectedUrl("/catalogue/products/1"));

    WireMock.verify(WireMock.postRequestedFor(WireMock.urlPathMatching("/catalogue-api/products"))
            .withRequestBody(WireMock.equalToJson(
                    """
                           {"title": "Товар", "details": "Описание товара"}
                           """)));
  }

  @Test
  void createProduct_RequestIsInvalid_ReturnsNewProductPage() throws Exception {
    // given
    var requestBuilder = MockMvcRequestBuilders.post("/catalogue/products/create")
            .param("title", "   ")
            .with(user("j.dewar").roles("MANAGER"))
            .with(csrf());

    WireMock.stubFor(WireMock.post(WireMock.urlPathMatching("/catalogue-api/products"))
            .withRequestBody(WireMock.equalToJson("""
                        {
                            "title": "   ",
                            "details": null
                        }"""))
            .willReturn(WireMock.badRequest()
                    .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PROBLEM_JSON_VALUE)
                    .withBody("""
                                {
                                    "errors": ["Ошибка 1", "Ошибка 2"]
                                }""")));

    // when
    this.mockMvc.perform(requestBuilder)
            // then
            .andDo(print())
            .andExpectAll(
                    status().isBadRequest(),
                    view().name("catalogue/products/new_product"),
                    model().attribute("payload", new NewProductPayload("   ", null)),
                    model().attribute("errors", List.of("Ошибка 1", "Ошибка 2"))
            );

    WireMock.verify(WireMock.postRequestedFor(WireMock.urlPathMatching("/catalogue-api/products"))
            .withRequestBody(WireMock.equalToJson("""
                        {
                            "title": "   ",
                            "details": null
                        }""")));
  }
}