package ag.selmag.catalogue.controller.payload;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UpdateProductPayload(
        @NotNull(message = "{catalogue.products.update.errors.title_is_null}")
        @Size(min = 2, max = 50,  message = "{catalogue.products.update.errors.title_size_is_invalid}")
        String title,

        @Size(max = 50, message = "{catalogue.products.update.errors.details_size_is_invalid}")
        String details) {
}
