package ag.selmag.manager.controller;

import ag.selmag.manager.controller.client.BadRequestException;
import ag.selmag.manager.controller.client.ProductsRestClient;
import ag.selmag.manager.controller.payload.NewProductPayload;
import ag.selmag.manager.entity.Product;
import lombok.RequiredArgsConstructor;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RequiredArgsConstructor
@Controller
@RequestMapping("catalogue/products")
public class ProductsController {
  private final ProductsRestClient productsRestClient;

  @GetMapping("list")
  public String getProductsList(Model model, @RequestParam(name = "filter", required = false) String filter) {
    model.addAttribute("products", this.productsRestClient.findAllProducts(filter));
    model.addAttribute("filter", filter);
    return "catalogue/products/list";
  }

  @GetMapping("create")
  public String getNewProductsPage() {
    return "catalogue/products/new_product";
  }

  @PostMapping("create")
  public String createProduct(NewProductPayload payload,
                              Model model) {
    try {
      Product product = this.productsRestClient.createProduct(payload.title(), payload.details());
      return "redirect:/catalogue/products/%d".formatted(product.id());
    } catch (BadRequestException e){
      model.addAttribute("payload", payload);
      model.addAttribute("errors", e.getErrors());
      return "catalogue/products/new_product";
    }
    }
}
