package ru.korostelev.feedbaack.controller;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.util.UriComponentsBuilder;
import ru.korostelev.feedback.controller.FavouriteProductsRestController;
import ru.korostelev.feedback.controller.payload.NewFavouriteProductPayload;
import ru.korostelev.feedback.entity.FavouriteProduct;
import ru.korostelev.feedback.service.FavouriteProductsService;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Модульные тесты FavouriteProductsRestController")
public class FavouriteProductsRestControllerTest {

    @Mock
    private FavouriteProductsService favouriteProductsService;

    @Mock
    private BindingResult bindingResult;

    @InjectMocks
    private FavouriteProductsRestController favouriteProductsController;

    @Test
    void findFavouriteProductsTest() {
        List<FavouriteProduct> mockProducts = Arrays.asList(
                new FavouriteProduct(UUID.randomUUID(), 1),
                new FavouriteProduct(UUID.randomUUID(), 2));
        when(favouriteProductsService.findFavouriteProducts()).thenReturn(mockProducts);

        List<FavouriteProduct> result = favouriteProductsController.findFavouriteProducts();

        assertEquals(mockProducts.size(), result.size());
        assertEquals(mockProducts, result);
    }

    @Test
    void findFavouriteProductByProductIdSuccess() {
        int productId = 1;
        FavouriteProduct mockProduct = new FavouriteProduct(UUID.randomUUID(), 1);
        when(favouriteProductsService.findFavouriteProductByProduct(productId)).thenReturn(mockProduct);

        FavouriteProduct result = favouriteProductsController.findFavouriteProductByProductId(productId);

        assertNotNull(result);
        assertEquals(mockProduct, result);
        verify(favouriteProductsService, times(1)).findFavouriteProductByProduct(productId);
    }

    @Test
    void findFavouriteProductByProductIdNotFound() {
        int productId = 999;
        when(favouriteProductsService.findFavouriteProductByProduct(productId)).thenReturn(null);

        FavouriteProduct result = favouriteProductsController.findFavouriteProductByProductId(productId);

        assertNull(result);
        verify(favouriteProductsService, times(1)).findFavouriteProductByProduct(productId);
    }

    @Test
    void addProductToFavouritesSuccess() throws BindException {
        NewFavouriteProductPayload payload = new NewFavouriteProductPayload(1);
        FavouriteProduct mockProduct = new FavouriteProduct(UUID.randomUUID(), 1);
        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.newInstance();

        when(bindingResult.hasErrors()).thenReturn(false);
        when(favouriteProductsService.addProductToFavourites(payload.id())).thenReturn(mockProduct);

        ResponseEntity<FavouriteProduct> response = favouriteProductsController
                .addProductToFavourites(payload, bindingResult, uriComponentsBuilder);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(Objects.requireNonNull(response.getHeaders().getLocation()));
        assertTrue(response.getHeaders().getLocation().toString()
                .contains("feedback-api/favourite-products/" + mockProduct.getUuid()));
        assertEquals(mockProduct, response.getBody());
        verify(favouriteProductsService, times(1)).addProductToFavourites(payload.id());
    }

    @Test
    void addProductToFavouritesBindException() {
        NewFavouriteProductPayload payload = new NewFavouriteProductPayload(1);
        when(bindingResult.hasErrors()).thenReturn(true);

        assertThrows(BindException.class, () ->
                favouriteProductsController.addProductToFavourites(
                        payload, bindingResult, UriComponentsBuilder.newInstance()));
        verify(favouriteProductsService, never()).addProductToFavourites(anyInt());
    }

    @Test
    void removeProductFromFavouritesSuccess() {
        int productId = 1;

        ResponseEntity<Void> response = favouriteProductsController.removeProductFromFavourites(productId);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(favouriteProductsService, times(1)).removeProductFromFavourites(productId);
    }

    @Test
    void removeProductFromFavouritesServiceException() {
        int productId = 1;
        doThrow(new IllegalArgumentException("ProductNotFound"))
                .when(favouriteProductsService).removeProductFromFavourites(productId);

        assertThrows(IllegalArgumentException.class, () ->
                favouriteProductsController.removeProductFromFavourites(productId));
        verify(favouriteProductsService, times(1)).removeProductFromFavourites(productId);
    }
}
