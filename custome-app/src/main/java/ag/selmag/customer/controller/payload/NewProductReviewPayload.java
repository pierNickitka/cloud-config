package ag.selmag.customer.controller.payload;


public record NewProductReviewPayload(
        Integer rating,
        String review) {}
