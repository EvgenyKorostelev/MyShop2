package ru.korostelev.feedback.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.korostelev.feedback.entity.ProductReview;

import java.util.List;
import java.util.UUID;

public interface ProductReviewsRepository extends JpaRepository<ProductReview, UUID> {

    List<ProductReview> findAllById(int id);
}
