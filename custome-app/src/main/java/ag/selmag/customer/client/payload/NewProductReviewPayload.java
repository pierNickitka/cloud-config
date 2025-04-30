package ag.selmag.customer.client.payload;

public record NewProductReviewPayload(Integer productId, Integer rating, String review) {
}
