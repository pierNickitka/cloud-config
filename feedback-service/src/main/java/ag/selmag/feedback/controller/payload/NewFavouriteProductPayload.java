package ag.selmag.feedback.controller.payload;

import jakarta.validation.constraints.NotNull;

public record NewFavouriteProductPayload(
        @NotNull(message = "{feedback.products.favourites.reviews.create.errors.product_id_is_null}")
        Integer productId)   {
}
