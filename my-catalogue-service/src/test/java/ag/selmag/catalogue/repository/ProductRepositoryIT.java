package ag.selmag.catalogue.repository;

import ag.selmag.catalogue.entity.Product;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles(value = {"standalone","test"})
@DataJpaTest
@Sql("/sql/products.sql")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ProductRepositoryIT {

  @Autowired
  ProductRepository productRepository;

  @Test
  void findAllByTitleIgnoreCase(){
    var filter = "%шоколадка%";

   var result = this.productRepository.findAllByTitleLikeIgnoreCase(filter);

    assertEquals(List.of(new Product(2, "Шоколадка", "Очень вкусная шоколадка")),result);
  }

}