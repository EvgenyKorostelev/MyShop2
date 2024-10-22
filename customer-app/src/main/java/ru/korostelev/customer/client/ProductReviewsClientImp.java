package ru.korostelev.customer.client;

import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ProblemDetail;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import ru.korostelev.customer.client.exception.BadRequestException;
import ru.korostelev.customer.client.payload.NewProductReviewPayload;
import ru.korostelev.customer.entity.ProductReview;

import java.util.List;

@RequiredArgsConstructor
public class ProductReviewsClientImp implements ProductReviewsClient {

    private static final ParameterizedTypeReference<List<ProductReview>> REVIEW_PRODUCTS_TYPE_REFERENCE =
            new ParameterizedTypeReference<>() {
            };

    private final RestClient restClient;

    @Override
    public List<ProductReview> findProductReviewsByProductId(Integer id) {
        return this.restClient
                .get()
                .uri("/feedback-api/product-reviews/by-product-id/{id}", id)
                .retrieve()
                .body(REVIEW_PRODUCTS_TYPE_REFERENCE);
    }

    @Override
    public ProductReview createProductReview(Integer id, Integer rating, String review) {
        try {
            return this.restClient
                    .post()
                    .uri("/feedback-api/product-reviews")
                    .body(new NewProductReviewPayload(id, rating, review))
                    .retrieve()
                    .body(ProductReview.class);
        } catch (HttpClientErrorException.BadRequest exception) {
            ProblemDetail problemDetail = exception.getResponseBodyAs(ProblemDetail.class);
            throw new BadRequestException((List<String>) problemDetail.getProperties().get("errors"));
        }
    }
}
