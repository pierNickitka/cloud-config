package ag.selmag.catalogue.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class Product {
  private Integer id;

  private String title;

  private String details;
}
