package ag.selmag.manager.controller;

import ag.selmag.manager.controller.payload.NewProductPayload;
import ag.selmag.manager.entity.Product;
import ag.selmag.manager.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.Banner;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@Controller
@RequestMapping("catalogue/products")
public class ProductsController {
  private final ProductService productService;

  @GetMapping("list")
  public String getProductsList(Model model) {
    model.addAttribute("products", productService.findAllProducts());
    return "catalogue/products/list";
  }

  @GetMapping("create")
  public String getNewProductsPage() {
    return "catalogue/products/new_product";
  }

  @PostMapping("create")
  public String createProduct(@Valid NewProductPayload payload,
                              BindingResult bindingResult,
                              Model model) {
    if(bindingResult.hasErrors()){
      model.addAttribute("payload", payload);
      model.addAttribute("errors", bindingResult.getAllErrors().stream()
              .map(ObjectError::getDefaultMessage)
              .toList());
      return "catalogue/products/new_product";
    }else {
      Product product = this.productService.createProduct(payload.title(), payload.details());
      return "redirect:/catalogue/products/%d".formatted(product.getId());
    }
  }
}
