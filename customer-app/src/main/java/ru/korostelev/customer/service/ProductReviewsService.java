package ru.korostelev.customer.service;

import ru.korostelev.customer.entity.ProductReview;

import java.util.List;

public interface ProductReviewsService {


    ProductReview createProductReview(int productId, int rating, String review);

    List<ProductReview> findProductReviewsByProduct(int productId);
}
