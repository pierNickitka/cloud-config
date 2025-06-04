package ag.selmag.catalogue.controller;

import ag.selmag.catalogue.controller.payload.NewProductPayload;
import ag.selmag.catalogue.entity.Product;
import ag.selmag.catalogue.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
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
  public Iterable<Product> findProducts(@RequestParam(name = "filter" ,required = false) String filter){
//    LoggerFactory.getLogger(ProductsRestController.class)
//            .info("principal {}", ((JwtAuthenticationToken)principal).getToken().getClaimAsString("email") );
    return this.productService.findAllProducts("%"+filter+"%");
  }

  @PostMapping
  public ResponseEntity<?> createProduct(@Valid @RequestBody NewProductPayload payload,
                                               BindingResult bindingResult,
                                               UriComponentsBuilder uriBuilder) throws BindException {
    if(bindingResult.hasErrors()){
      if(bindingResult instanceof BindException ex){
        throw ex;
      } else throw new BindException(bindingResult);
    } else {
      Product product = this.productService.createProduct(payload.title(),payload.details());
      return ResponseEntity
              .created(uriBuilder.replacePath("/catalogue-api/products/{id}").build(Map.of("id", product.getId())))
              .body(product);
    }

  }
}
