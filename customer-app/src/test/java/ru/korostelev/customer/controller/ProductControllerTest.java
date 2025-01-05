package ru.korostelev.customer.controller;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;
import ru.korostelev.customer.client.BasketClient;
import ru.korostelev.customer.client.FavouriteProductsClient;
import ru.korostelev.customer.client.ProductReviewsClient;
import ru.korostelev.customer.client.ProductsClient;
import ru.korostelev.customer.client.exception.BadRequestException;
import ru.korostelev.customer.controller.payload.NewProductReviewPayload;
import ru.korostelev.customer.entity.BasketProduct;
import ru.korostelev.customer.entity.FavouriteProduct;
import ru.korostelev.customer.entity.Product;
import ru.korostelev.customer.entity.ProductReview;

import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Модульные тесты ProductController")
public class ProductControllerTest {

    @Mock
    private ProductsClient productsClient;

    @Mock
    private BasketClient basketClient;

    @Mock
    private FavouriteProductsClient favouriteProductsClient;

    @Mock
    private ProductReviewsClient productReviewsClient;

    @Mock
    private Model model;

    @InjectMocks
    private ProductController productController;

    @Test
    @DisplayName("product вернет товар")
    void productFound() {
        int productId = 1;
        Product expectedProduct = new Product(1, "Molotok", "Bolshoy");
        when(productsClient.findProduct(productId)).thenReturn(expectedProduct);

        Product result = productController.product(productId);

        assertEquals(expectedProduct, result);
        verify(productsClient).findProduct(productId);
    }

    @Test
    @DisplayName("product страницу ошибки 404")
    void productNotFound() {
        int productId = 1;
        when(productsClient.findProduct(productId)).thenReturn(null);

        NoSuchElementException exception = assertThrows(NoSuchElementException.class,
                () -> productController.product(productId));
        assertEquals("customer.products.error.not_found", exception.getMessage());
        verify(productsClient).findProduct(productId);
    }

    @Test
    @DisplayName("getProduct вернет страницу товара, который не в корзине и не в избранном")
    void getProductNotInBasketOrFavourites() {
        int productId = 1;
        List<ProductReview> reviews = List.of(
                new ProductReview(UUID.randomUUID(), 1, 5, "Good product"),
                new ProductReview(UUID.randomUUID(), 2, 4, "Value for money"));
        when(productReviewsClient.findProductReviewsByProductId(productId)).thenReturn(reviews);
        when(basketClient.findProductsInBasket()).thenReturn(Collections.emptyList());
        when(basketClient.findBasketProductById(productId)).thenReturn(null);
        when(favouriteProductsClient.findFavouriteProductByProductId(productId)).thenReturn(null);

        String viewName = productController.getProduct(productId, model);

        assertEquals("customer/products/product", viewName);
        verify(model).addAttribute("inFavourite", false);
        verify(model).addAttribute("inBasket", false);
        verify(model).addAttribute("reviews", reviews);
        verify(model).addAttribute("basket", 0);
        verify(productReviewsClient).findProductReviewsByProductId(productId);
        verify(basketClient).findProductsInBasket();
        verify(basketClient).findBasketProductById(productId);
        verify(favouriteProductsClient).findFavouriteProductByProductId(productId);
    }

    @Test
    @DisplayName("getProduct вернет страницу товара, который в корзине и в избранном")
    void getProductInBasketAndFavourites() {
        int productId = 1;
        List<ProductReview> reviews = List.of(
                new ProductReview(UUID.randomUUID(), 1, 5, "Good product"),
                new ProductReview(UUID.randomUUID(), 2, 4, "Value for money"));
        List<BasketProduct> basketProducts = List.of(
                new BasketProduct(1, 10),
                new BasketProduct(2, 1));
        when(productReviewsClient.findProductReviewsByProductId(productId)).thenReturn(reviews);
        when(basketClient.findProductsInBasket()).thenReturn(basketProducts);
        when(basketClient.findBasketProductById(productId))
                .thenReturn(new BasketProduct(1, 10));
        when(favouriteProductsClient.findFavouriteProductByProductId(productId))
                .thenReturn(new FavouriteProduct(UUID.randomUUID(), 1));

        String viewName = productController.getProduct(productId, model);

        assertEquals("customer/products/product", viewName);
        verify(model).addAttribute("inFavourite", true);
        verify(model).addAttribute("inBasket", true);
        verify(model).addAttribute("reviews", reviews);
        verify(model).addAttribute("basket", 2);
        verify(productReviewsClient).findProductReviewsByProductId(productId);
        verify(basketClient).findProductsInBasket();
        verify(basketClient).findBasketProductById(productId);
        verify(favouriteProductsClient).findFavouriteProductByProductId(productId);
    }

    @Test
    @DisplayName("addProductToBasket добавит товар в корзину успешно, останется на странице товара")
    void addProductToBasketSuccess() {
        Product product = new Product(1, "Molotok", "Bolshoy");
        when(basketClient.addProductToBasket(product.id(), 1))
                .thenReturn(new BasketProduct(product.id(), 1));

        String result = productController.addProductToBasket(product, model);

        assertEquals("redirect:/customer/products/1", result);
        verify(basketClient).addProductToBasket(product.id(), 1);
        verifyNoInteractions(model);
    }

    @Test
    @DisplayName("addProductToBasket ошибка при добавлении товара в корзину")
    void addProductToBasketBadRequestException() {
        Product product = new Product(1, "Molotok", "Bolshoy");
        BadRequestException exception = new BadRequestException(List.of("error", "Product is unavailable"));
        doThrow(exception).when(basketClient).addProductToBasket(product.id(), 1);

        String result = productController.addProductToBasket(product, model);

        assertEquals("redirect:/customer/products/1", result);
        verify(basketClient).addProductToBasket(product.id(), 1);
        verify(model).addAttribute("errors", exception.getErrors());
    }

    @Test
    @DisplayName("removeFromBasket удалит товар из корзины")
    void removeFromBasketSuccess() {
        Product product = new Product(1, "Molotok", "Bolshoy");
        doNothing().when(basketClient).removeProductFromBasket(product.id());

        String result = productController.removeFromBasket(product);

        assertEquals("redirect:/customer/products/basket", result);
        verify(basketClient).removeProductFromBasket(product.id());
    }

    @Test
    @DisplayName("removeFromBasket ошибка удаления товара из корзины")
    void removeFromBasketExceptionHandling() {
        Product product = new Product(1, "Molotok", "Bolshoy");
        doThrow(new RuntimeException("Error while removing product"))
                .when(basketClient).removeProductFromBasket(product.id());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> productController.removeFromBasket(product));
        assertEquals("Error while removing product", exception.getMessage());
        verify(basketClient).removeProductFromBasket(product.id());
    }

    @Test
    @DisplayName("addProductToFavourites добавление товара в избранное успешное")
    void addProductToFavouritesSuccess() {
        Product product = new Product(1, "Molotok", "Bolshoy");
        when(favouriteProductsClient.addProductToFavourites(product.id()))
                .thenReturn(new FavouriteProduct(UUID.randomUUID(), product.id()));

        String result = productController.addProductToFavourites(product, model);

        assertEquals("redirect:/customer/products/1", result);
        verify(favouriteProductsClient).addProductToFavourites(product.id());
        verifyNoInteractions(model);
    }

    @Test
    @DisplayName("addProductToFavourites ошибка добавления товара в избранное")
    void addProductToFavouritesBadRequestException() {
        Product product = new Product(1, "Molotok", "Bolshoy");
        BadRequestException exception = new BadRequestException(List.of("error", "Product already in favourites"));
        doThrow(exception).when(favouriteProductsClient).addProductToFavourites(product.id());

        String result = productController.addProductToFavourites(product, model);

        assertEquals("redirect:/customer/products/1", result);
        verify(favouriteProductsClient).addProductToFavourites(product.id());
        verify(model).addAttribute("errors", exception.getErrors());
    }

    @Test
    @DisplayName("removeProductFromFavourites удаление товара из избранного успешное")
    void removeProductFromFavouritesSuccess() {
        Product product = new Product(1, "Molotok", "Bolshoy");
        doNothing().when(favouriteProductsClient).removeProductFromFavourites(product.id());

        String result = productController.removeProductFromFavourites(product);

        assertEquals("redirect:/customer/products/1", result);
        verify(favouriteProductsClient).removeProductFromFavourites(product.id());
    }

    @Test
    @DisplayName("removeProductFromFavourites ошибка удаления товара из избранного")
    void removeProductFromFavouritesExceptionHandling() {
        Product product = new Product(1, "Molotok", "Bolshoy");
        doThrow(new RuntimeException("Error while removing from favourites"))
                .when(favouriteProductsClient).removeProductFromFavourites(product.id());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> productController.removeProductFromFavourites(product));
        assertEquals("Error while removing from favourites", exception.getMessage());
        verify(favouriteProductsClient).removeProductFromFavourites(product.id());
    }

    @Test
    @DisplayName("createReview создаст отзыв")
    void createReviewSuccess() {
        Product product = new Product(1, "Molotok", "Bolshoy");
        NewProductReviewPayload payload = new NewProductReviewPayload(5, "Great product!");
        when(productReviewsClient.createProductReview(product.id(), payload.rating(), payload.review()))
                .thenReturn(new ProductReview(UUID.randomUUID(), product.id(), payload.rating(), payload.review()));

        String result = productController.createReview(product, payload, model);

        assertEquals("redirect:/customer/products/1", result);
        verify(productReviewsClient).createProductReview(product.id(), payload.rating(), payload.review());
        verifyNoInteractions(model);
    }

    @Test
    @DisplayName("createReview ошибка создания отзыва")
    void createReviewBadRequestException() {
        Product product = new Product(1, "Molotok", "Bolshoy");
        NewProductReviewPayload payload = new NewProductReviewPayload(3, "Average product");
        BadRequestException exception = new BadRequestException(List.of("error", "Invalid review"));
        doThrow(exception).when(productReviewsClient).createProductReview(product.id(), payload.rating(), payload.review());
        when(favouriteProductsClient.findFavouriteProductByProductId(product.id())).thenReturn(null);

        String result = productController.createReview(product, payload, model);

        assertEquals("customer/products/product", result);
        verify(productReviewsClient).createProductReview(product.id(), payload.rating(), payload.review());
        verify(model).addAttribute("inFavourite", false);
        verify(model).addAttribute("payload", payload);
        verify(model).addAttribute("errors", exception.getErrors());
    }

    @Test
    @DisplayName("createReview ошибка создания отзыва")
    void createReviewBadRequestExceptionWithFavourite() {
        Product product = new Product(1, "Molotok", "Bolshoy");
        FavouriteProduct favouriteProduct = new FavouriteProduct(UUID.randomUUID(), product.id());
        NewProductReviewPayload payload = new NewProductReviewPayload(2, "Not good");
        BadRequestException exception = new BadRequestException(List.of("error", "Invalid review"));
        doThrow(exception).when(productReviewsClient).createProductReview(product.id(), payload.rating(), payload.review());
        when(favouriteProductsClient.findFavouriteProductByProductId(product.id())).thenReturn(favouriteProduct);

        String result = productController.createReview(product, payload, model);

        assertEquals("customer/products/product", result);
        verify(productReviewsClient).createProductReview(product.id(), payload.rating(), payload.review());
        verify(model).addAttribute("inFavourite", true);
        verify(model).addAttribute("payload", payload);
        verify(model).addAttribute("errors", exception.getErrors());
    }
}
