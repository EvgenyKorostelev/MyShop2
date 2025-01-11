package ru.korostelev.feedbaack.controller;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.util.UriComponentsBuilder;
import ru.korostelev.feedback.controller.ProductReviewsRestController;
import ru.korostelev.feedback.controller.payload.NewProductReviewPayload;
import ru.korostelev.feedback.entity.ProductReview;
import ru.korostelev.feedback.service.ProductReviewsService;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Модульные тесты ProductReviewsRestController")
public class ProductReviewsRestControllerTest {

    @Mock
    private ProductReviewsService productReviewsService;

    @Mock
    private BindingResult bindingResult;

    @InjectMocks
    private ProductReviewsRestController productReviewsController;

    @Test
    void testFindProductReviewsByProductId() {
        int productId = 1;
        List<ProductReview> mockReviews = Arrays.asList(
                new ProductReview(UUID.randomUUID(), 1, 2, "Review1"),
                new ProductReview(UUID.randomUUID(), 1, 5, "Review2")
        );

        when(productReviewsService.findProductReviewsByProduct(productId)).thenReturn(mockReviews);

        List<ProductReview> result = productReviewsController.findProductReviewsByProductId(productId);

        assertEquals(2, result.size());
        assertEquals("Review1", result.get(0).getReview());
        assertEquals(2, result.get(0).getRating());
        assertEquals("Review2", result.get(1).getReview());
        assertEquals(5, result.get(1).getRating());

        verify(productReviewsService, times(1)).findProductReviewsByProduct(productId);
    }

    @Test
    void testCreateProductReview_Success() throws BindException {
        NewProductReviewPayload payload = new NewProductReviewPayload(1, 5, "ExcellentProduct!");
        ProductReview mockReview = new ProductReview(UUID.randomUUID(), 1, 5, "ExcellentProduct!");

        when(bindingResult.hasErrors()).thenReturn(false);
        when(productReviewsService.createProductReview(payload.id(), payload.rating(), payload.review()))
                .thenReturn(mockReview);

        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.newInstance();

        ResponseEntity<ProductReview> response = productReviewsController.createProductReview(
                payload, bindingResult, uriComponentsBuilder);

        assertEquals(201, response.getStatusCodeValue());
        assertNotNull(Objects.requireNonNull(response.getBody()));
        assertEquals("ExcellentProduct!", response.getBody().getReview());
        assertEquals(5, response.getBody().getRating());
        assertEquals(mockReview.getUuid(), response.getBody().getUuid());

        verify(productReviewsService, times(1))
                .createProductReview(payload.id(), payload.rating(), payload.review());
    }

    @Test
    void testCreateProductReview_BindingErrors() {
        NewProductReviewPayload payload = new NewProductReviewPayload(1, 0, "");
        when(bindingResult.hasErrors()).thenReturn(true);

        assertThrows(BindException.class, () ->
                productReviewsController.createProductReview(
                        payload, bindingResult, UriComponentsBuilder.newInstance()));

        verify(productReviewsService, never()).createProductReview(anyInt(), anyInt(), anyString());
    }
}
