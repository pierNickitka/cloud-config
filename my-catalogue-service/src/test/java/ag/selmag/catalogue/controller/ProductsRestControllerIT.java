//package ag.selmag.catalogue.controller;
//
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.MediaType;
//import org.springframework.security.web.header.Header;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.test.context.jdbc.Sql;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
//import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.Locale;
//
//import static org.springframework.http.converter.json.Jackson2ObjectMapperBuilder.json;
//import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
//import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;
//import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//@Transactional
//@SpringBootTest
//@ActiveProfiles(value = {"standalone","test"})
//@AutoConfigureMockMvc
//class ProductsRestControllerIT {
//
//  @Autowired
//  MockMvc mockMvc;
//
//  @Sql(scripts = "/sql/products.sql", executionPhase = BEFORE_TEST_METHOD)
//  @Test
//  void findProducts_ReturnsProductsList() throws Exception {
//    // given
//    var requestBuilder = MockMvcRequestBuilders.get("/catalogue-api/products")
//            .param("filter", "товар")
//            .with(jwt().jwt(builder -> builder.claim("scope", "view_catalogue")));
//
//    // when
//    this.mockMvc.perform(requestBuilder)
//            // then
//            .andDo(print())
//            .andExpectAll(
//                    status().isOk(),
//                    content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
//                    content().json("""
//                                [
//                                {"id": 1, "title": "Товар №1", "details": "Описание товара №1"},
//                                {"id": 3, "title": "Товар №3", "details": "Описание товара №3"}
//                                ]""")
//            );
//  }
//
//  @Test
//  void createProduct_RequestIsValid_ReturnsNewProduct() throws Exception{
//    var requestBuilder = MockMvcRequestBuilders
//            .post("/catalogue-api/products")
//            .contentType(MediaType.APPLICATION_JSON)
//            .content("""
//                    {"title": "Еще один новый товар", "details": "Какое-то описание нового товара"}
//                    """)
//            .with(jwt().jwt(builder -> builder.claim("scope","edit_catalogue")));
//
//    this.mockMvc.perform(requestBuilder)
//            .andDo(print())
//            .andExpectAll(
//                    status().isCreated(),
//                    content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
//                    header().string(HttpHeaders.LOCATION,"http://localhost/catalogue-api/products/1"),
//                    content().json("""
//                            {"id": 1, "title": "Еще один новый товар", "details": "Какое-то описание нового товара"}
//                            """)
//            );
//  }
//
//  @Test
//  void createProduct_RequestIsInvalid_ReturnsNewProduct() throws Exception{
//    var requestBuilder = MockMvcRequestBuilders
//            .post("/catalogue-api/products")
//            .contentType(MediaType.APPLICATION_JSON)
//            .locale(new Locale("ru", "RU"))
//            .content("""
//                    {"title": " ", "details": null}
//                    """)
//            .with(jwt().jwt(builder -> builder.claim("scope","edit_catalogue")));
//
//    this.mockMvc.perform(requestBuilder)
//            .andDo(print())
//            .andExpectAll(
//                    status().isBadRequest(),
//                    content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON),
//                    content().json("""
//                            { "errors": [
//                              "Название товара должно быть от 3 до 50 символов"
//                            ]
//                            }
//                            """)
//            );
//  }
//
//  @Test
//  void createProduct_RequestNotAuthorized_ReturnsForbidden() throws Exception{
//    var requestBuilder = MockMvcRequestBuilders
//            .post("/catalogue-api/products")
//            .contentType(MediaType.APPLICATION_JSON)
//            .locale(new Locale("ru", "RU"))
//            .content("""
//                    {"title": " ", "details": null}
//                    """)
//            .with(jwt().jwt(builder -> builder.claim("scope","view_catalogue")));
//
//    this.mockMvc.perform(requestBuilder)
//            .andDo(print())
//            .andExpectAll(
//                    status().isForbidden()
//            );
//  }
//}