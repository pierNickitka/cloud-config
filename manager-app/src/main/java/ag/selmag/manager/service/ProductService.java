package ag.selmag.manager.service;

import ag.selmag.manager.entity.Product;
import ag.selmag.manager.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

public interface ProductService {

  List<Product> findAllProducts();

  Product createProduct(String title, String details);

  Optional<Product> findProduct(int productId);

  void updateProduct(Integer id, String title, String details);

  void deleteProduct(Integer id);
}
