package ag.selmag.catalogue.controller;

import ag.selmag.catalogue.controller.payload.NewProductPayload;
import ag.selmag.catalogue.entity.Product;
import ag.selmag.catalogue.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.StringToClassMapItem;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
//import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.security.Principal;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("catalogue-api/products")
public class ProductsRestController {
  private final ProductService productService;

  @GetMapping
  @Operation(security = @SecurityRequirement(name = "keycloak"))
  public Iterable<Product> findProducts(@RequestParam(name = "filter", required = false) String filter) {
    return this.productService.findAllProducts(filter);
  }

  @PostMapping
  @Operation(
          security = @SecurityRequirement(name = "keycloak"),
          requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                  content = @Content(
                          mediaType = MediaType.APPLICATION_JSON_VALUE,
                          schema = @Schema(
                                  type = "object",
                                  properties = {
                                          @StringToClassMapItem(key = "title", value = String.class),
                                          @StringToClassMapItem(key = "details", value = String.class)
                                  }
                          )
                  )
          ),
          responses = {
                  @ApiResponse(
                          responseCode = "201",
                          headers = @Header(name = "Content-Type", description = "Тип данных"),
                          content = {
                                  @Content(
                                          mediaType = MediaType.APPLICATION_JSON_VALUE,
                                          schema = @Schema(
                                                  type = "object",
                                                  properties = {
                                                          @StringToClassMapItem(key = "id", value = Integer.class),
                                                          @StringToClassMapItem(key = "title", value = String.class),
                                                          @StringToClassMapItem(key = "details", value = String.class)
                                                  }
                                          )
                                  )
                          }
                  )
          })
  public ResponseEntity<?> createProduct(@Valid @RequestBody NewProductPayload payload,
                                         BindingResult bindingResult,
                                         UriComponentsBuilder uriBuilder) throws BindException {
    if (bindingResult.hasErrors()) {
      if (bindingResult instanceof BindException ex) {
        throw ex;
      } else throw new BindException(bindingResult);
    } else {
      Product product = this.productService.createProduct(payload.title(), payload.details());
      return ResponseEntity
              .created(uriBuilder.replacePath("/catalogue-api/products/{id}").build(Map.of("id", product.getId())))
              .body(product);
    }

  }
}
