package ru.korostelev.customer.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.korostelev.customer.entity.ProductReview;
import ru.korostelev.customer.repository.ProductReviewsRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductReviewsServiceImp implements ProductReviewsService {

    private final ProductReviewsRepository productReviewsRepository;

    @Override
    public ProductReview createProductReview(int productId, int rating, String review) {
        return this.productReviewsRepository.save(new ProductReview(UUID.randomUUID(), productId, rating, review));
    }

    @Override
    public List<ProductReview> findProductReviewsByProduct(int productId) {
        return this.productReviewsRepository.findAllByProductId(productId);
    }
}
