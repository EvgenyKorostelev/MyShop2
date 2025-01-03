package ru.korostelev.manager.controller;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;
import ru.korostelev.manager.client.ProductsRestClient;
import ru.korostelev.manager.client.exception.BadRequestException;
import ru.korostelev.manager.controller.payload.UpdateProductPayload;
import ru.korostelev.manager.entity.Product;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Модульные тесты ProductController")
public class ProductControllerTest {

    @Mock
    ProductsRestClient productsRestClient;

    @InjectMocks
    ProductController productController;

    @Mock
    private Model model;

    @Test
    @DisplayName("product вернет товар")
    void product_ShouldReturnProduct_WhenProductExists() {
        Integer productId = 1;
        Product product = new Product(productId, "testProduct", "testDescription");
        when(productsRestClient.findProduct(productId)).thenReturn(Optional.of(product));

        Product result = productController.product(productId);

        assertEquals(product, result);
        verify(productsRestClient).findProduct(productId);
    }

    @Test
    @DisplayName("product вернет ошибку товар не найден")
    void product_ShouldThrowException_WhenProductDoesNotExist() {
        Integer productId = 2;
        when(productsRestClient.findProduct(productId)).thenReturn(Optional.empty());

        NoSuchElementException exception = assertThrows(NoSuchElementException.class,
                () -> productController.product(productId));
        assertEquals("catalogue.errors.product.not_found", exception.getMessage());
        verify(productsRestClient).findProduct(productId);
    }

    @Test
    @DisplayName("deleteProduct удалит товар и вернет страницу списка товаров")
    void deleteProduct_ShouldRedirectAfterDeletion() {
        int productId = 1;
        Product product = new Product(productId, "testProduct", "testDescription");

        String result = productController.deleteProduct(product);

        assertEquals("redirect:/products/list", result);
        verify(productsRestClient).deleteProduct(productId);
    }

    @Test
    @DisplayName("updateProduct обновит информацию о товаре и перенаправит на страницу товара")
    void updateProduct_ShouldRedirectAfterSuccessfulUpdate() {
        int productId = 1;
        Product product = new Product(productId, "testProduct", "testDescription");
        UpdateProductPayload payload = new UpdateProductPayload("UpdatedTitle", "UpdatedDescription");

        String result = productController.updateProduct(product, payload, model);

        assertEquals("redirect:/products/1", result);
        verify(productsRestClient).updateProduct(productId, payload.title(), payload.description());
        verifyNoInteractions(model);
    }

    @Test
    @DisplayName("updateProduct если данные, для обновления не валидные, вернет страницу товара")
    void updateProduct_ShouldReturnEditView_WhenUpdateFails() {
        Integer productId = 1;
        Product product = new Product(productId, "testProduct", "testDescription");
        UpdateProductPayload payload = new UpdateProductPayload("InvalidTitle", "InvalidDescription");
        BadRequestException exception = new BadRequestException("ValidationFailed", List.of("error1", "error2"));
        doThrow(exception).when(productsRestClient).updateProduct(productId, payload.title(), payload.description());

        String result = productController.updateProduct(product, payload, model);

        assertEquals("products/product_edit", result);
        verify(productsRestClient).updateProduct(productId, payload.title(), payload.description());
        verify(model).addAttribute("payload", payload);
        verify(model).addAttribute("errors", exception.getErrors());
    }

}
