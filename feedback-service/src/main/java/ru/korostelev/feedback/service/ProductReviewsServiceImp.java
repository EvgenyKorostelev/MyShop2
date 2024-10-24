package ru.korostelev.feedback.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.korostelev.feedback.entity.ProductReview;
import ru.korostelev.feedback.repository.ProductReviewsRepository;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductReviewsServiceImp implements ProductReviewsService {

    private final ProductReviewsRepository productReviewsRepository;

    @Override
    @Transactional
    public ProductReview createProductReview(int id, int rating, String review) {
        return this.productReviewsRepository.save(new ProductReview(UUID.randomUUID(), id, rating, review));
    }

    @Override
    public List<ProductReview> findProductReviewsByProduct(int id) {
        return this.productReviewsRepository.findAllById(id);
    }
}
