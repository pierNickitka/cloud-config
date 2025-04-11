package ag.selmag.catalogue.repository;

import ag.selmag.catalogue.entity.Product;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class InMemoryProductRepository implements ProductRepository{
  private final List<Product> products = Collections.synchronizedList(new LinkedList<>());

/*
  public InMemoryProductRepository(){
    IntStream.range(1, 4)
            .forEach(i -> this.products.add
                    (new Product(i, "Товар N%d".formatted(i), "Описание N%d".formatted(i))));
  }
*/

  @Override
  public List<Product> findAll() {
    return Collections.unmodifiableList(products);
  }

  @Override
  public Product save(Product product) {
    product.setId(this.products.stream()
            .max(Comparator.comparingInt(Product::getId))
            .map(Product::getId).orElse(0));
    this.products.add(product);
    return product;
  }

  @Override
  public Optional<Product> findById(Integer productId) {
    return this.products.stream()
            .filter(product -> Objects.equals(productId, product.getId()))
            .findFirst();
  }

  @Override
  public void deleteById(Integer id) {
    this.products.removeIf(product -> Objects.equals(id, product.getId()));
  }
}
