package ag.selmag.catalogue.service;

import ag.selmag.catalogue.entity.Product;
import ag.selmag.catalogue.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.beans.Transient;
import java.util.*;

@Service
@RequiredArgsConstructor
public class DefaultProductService implements ProductService {
  private final ProductRepository productRepository;

  @Override
  public Iterable<Product> findAllProducts(String filter) {
    if (filter != null && !filter.isBlank()) {
      return this.productRepository.findAllByTitleLikeIgnoreCase("%" + filter + "%");
    } else {
      return this.productRepository.findAll();
    }
  }

  @Override
  @Transactional
  public Product createProduct(String title, String details) {
    return this.productRepository.save(new Product(null, title, details));
  }

  @Override
  public Optional<Product> findProduct(int productId) {
    return this.productRepository.findById(productId);
  }

  @Override
  @Transactional
  public void updateProduct(Integer id, String title, String details) {
    this.productRepository.findById(id)
            .ifPresentOrElse(product -> {
              product.setTitle(title);
              product.setDetails(details);
            }, () -> {
              throw new NoSuchElementException();
            });
  }

  @Override
  @Transactional
  public void deleteProduct(Integer id) {
    this.productRepository.deleteById(id);
  }
}
