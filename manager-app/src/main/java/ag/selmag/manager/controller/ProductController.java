package ag.selmag.manager.controller;

import ag.selmag.manager.controller.client.BadRequestException;
import ag.selmag.manager.controller.client.ProductsRestClient;
import ag.selmag.manager.controller.payload.UpdateProductPayload;
import ag.selmag.manager.entity.Product;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import java.util.Locale;
import java.util.NoSuchElementException;

@Controller
@RequiredArgsConstructor
@RequestMapping("catalogue/products/{productId:\\d+}")
public class ProductController {
  private final ProductsRestClient productsRestClient;

  private final MessageSource messageSource;

  @ModelAttribute("product")
  public Product product(@PathVariable("productId") int productId){
    return this.productsRestClient.findProduct(productId)
            .orElseThrow(() -> new NoSuchElementException("catalogue.errors.product.not_found"));
  }

  @GetMapping
  public String getProduct() {
    return "catalogue/products/product";
  }

  @GetMapping("edit")
  public String getProductEditPage(){
    return "catalogue/products/edit";
  }

  @PostMapping("edit")
  public String editProduct(@ModelAttribute(name = "product", binding = false) Product product,
                            UpdateProductPayload payload,
                            Model model,
                            HttpServletResponse  response){
      try {
        this.productsRestClient.updateProduct(product.id(), payload.title(), payload.details());
        return "redirect:/catalogue/products/%d".formatted(product.id());
      } catch (BadRequestException e){
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        model.addAttribute("payload", payload);
        model.addAttribute("errors", e.getErrors());
        return "catalogue/products/edit";
      }
    }

  @PostMapping("delete")
  public String deleteProduct(@ModelAttribute("product") Product product){
    this.productsRestClient.deleteProduct(product.id());
    return "redirect:/catalogue/products/list";
  }

  @ExceptionHandler(NoSuchElementException.class)
  public String handleNoSuchElementException(NoSuchElementException ex,
                                             Model model,
                                             HttpServletResponse response, Locale locale){
    response.setStatus(HttpStatus.NOT_FOUND.value());
    model.addAttribute("error",
            this.messageSource.getMessage(ex.getMessage(), new Object[0],
                    ex.getMessage(), locale));
    return "errors/404";
  }
}
