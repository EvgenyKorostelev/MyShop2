package ru.korostelev.customer.repository;

import org.springframework.stereotype.Repository;
import ru.korostelev.customer.entity.ProductReview;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

@Repository
public class ProductReviewsRepositoryImp implements ProductReviewsRepository{

    private final List<ProductReview> productReviews = Collections.synchronizedList(new LinkedList<>());

    @Override
    public ProductReview save(ProductReview productReview) {
        this.productReviews.add(productReview);
        return Mono.just(productReview);
    }

    @Override
    public List<ProductReview> findAllByProductId(int id) {
        return Flux.fromIterable(this.productReviews)
                .filter(productReview -> productReview.getProductId() == id);
    }
}
