package ru.korostelev.feedback.service;

import ru.korostelev.feedback.entity.ProductReview;

import java.util.List;

public interface ProductReviewsService {


    ProductReview createProductReview(int id, int rating, String review);

    List<ProductReview> findProductReviewsByProduct(int id);
}
