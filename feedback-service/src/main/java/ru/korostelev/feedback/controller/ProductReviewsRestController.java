package ru.korostelev.feedback.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import ru.korostelev.feedback.controller.payload.NewProductReviewPayload;
import ru.korostelev.feedback.entity.ProductReview;
import ru.korostelev.feedback.service.ProductReviewsService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("feedback-api/product-reviews")
@RequiredArgsConstructor
public class ProductReviewsRestController {

    private final ProductReviewsService productReviewsService;

    @GetMapping("by-product-id/{id:\\d+}")
    public List<ProductReview> findProductReviewsByProductId(@PathVariable("id") int id) {
        return this.productReviewsService.findProductReviewsByProduct(id);
    }

    @PostMapping
    public ResponseEntity<ProductReview> createProductReview(
            @Valid @RequestBody NewProductReviewPayload payload,
            BindingResult bindingResult,
            UriComponentsBuilder uriComponentsBuilder)
            throws BindException {
        if (bindingResult.hasErrors()) {
            if (bindingResult instanceof BindException exception) {
                throw exception;
            } else {
                throw new BindException(bindingResult);
            }
        } else {
            ProductReview productReview = this.productReviewsService.createProductReview(payload.id(),
                    payload.rating(), payload.review());
            return ResponseEntity
                    .created(uriComponentsBuilder
                            .replacePath("/feedback-api/product-reviews/{uuid}")
                            .build(Map.of("uuid", productReview.getUuid())))
                    .body(productReview);
        }
    }
}
