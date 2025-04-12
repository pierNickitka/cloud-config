package ag.selmag.catalogue.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@NamedQueries(
        @NamedQuery(name = "Product.findAllByTitleLikeIgnoreCase",
                query = """
                        select p from Product p where p.title ilike :filter
                        """)
)
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "t_product", schema = "catalogue")
public class Product {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column(name = "c_title")
  private String title;

  @Column(name = "c_detail")
  private String details;
}
