package ru.korostelev.customer.client;

import ru.korostelev.customer.entity.ProductReview;

import java.util.List;

public interface ProductReviewsClient {

    List<ProductReview> findProductReviewsByProductId(Integer id);

    ProductReview createProductReview(Integer id, Integer rating, String review);
}
