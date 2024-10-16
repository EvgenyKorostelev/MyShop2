package ru.korostelev.customer.repository;

import ru.korostelev.customer.entity.ProductReview;

import java.util.List;

public interface ProductReviewsRepository {

    ProductReview save(ProductReview productReview);

    List<ProductReview> findAllByProductId(int id);
}
