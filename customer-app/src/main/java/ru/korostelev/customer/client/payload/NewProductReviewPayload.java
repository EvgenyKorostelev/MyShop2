package ru.korostelev.customer.client.payload;

public record NewProductReviewPayload(Integer id, Integer rating, String review) {
}
