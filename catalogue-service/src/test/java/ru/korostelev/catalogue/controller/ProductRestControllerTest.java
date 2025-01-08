package ru.korostelev.catalogue.controller;


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
import ru.korostelev.catalogue.controller.payload.UpdateProductPayload;
import ru.korostelev.catalogue.entity.Product;
import ru.korostelev.catalogue.service.ProductService;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Модульные тесты ProductRestController")
public class ProductRestControllerTest {

    @Mock
    private ProductService productService;

    @Mock
    private BindingResult bindingResult;

    @Mock
    private UriComponentsBuilder uriComponentsBuilder;

    @InjectMocks
    private ProductRestController productController;

    @Test
    void getProductSuccess() {
        Integer productId = 1;
        Product expectedProduct = new Product(1, "Molotok", "Bolshoy");
        when(productService.findProductById(productId)).thenReturn(Optional.of(expectedProduct));

        Product result = productController.getProduct(productId);

        assertEquals(expectedProduct, result);
        verify(productService).findProductById(productId);
    }

    @Test
    void getProductProductNotFound() {
        Integer productId = 1;
        when(productService.findProductById(productId)).thenReturn(Optional.empty());

        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () ->
                productController.getProduct(productId));
        assertEquals("catalogue.errors.product.not_found", exception.getMessage());
        verify(productService).findProductById(productId);
    }

    @Test
    void updateProductSuccess() throws BindException {
        Integer productId = 1;
        UpdateProductPayload payload = new UpdateProductPayload("UpdatedTitle", "UpdatedDescription");
        when(bindingResult.hasErrors()).thenReturn(false);

        ResponseEntity<?> response = productController.updateProduct(productId, payload, bindingResult);

        assertEquals(ResponseEntity.noContent().build(), response);
        verify(bindingResult).hasErrors();
        verify(productService).updateProduct(productId, payload.title(), payload.description());
    }

    @Test
    void updateProductBindingResultErrors() {
        Integer productId = 1;
        UpdateProductPayload payload = new UpdateProductPayload("", "");
        when(bindingResult.hasErrors()).thenReturn(true);

        assertThrows(BindException.class, () ->
                productController.updateProduct(productId, payload, bindingResult));
        verify(bindingResult).hasErrors();
        verifyNoInteractions(productService);
    }

    @Test
    void updateProductBindExceptionFromBindingResult() {
        Integer productId = 1;
        UpdateProductPayload payload = new UpdateProductPayload("", "");
        BindException exception = new BindException(bindingResult);
        when(bindingResult.hasErrors()).thenReturn(true);

        BindException thrown = assertThrows(BindException.class, () ->
                productController.updateProduct(productId, payload, bindingResult));
        assertEquals(exception.getMessage(), thrown.getMessage());
        verify(bindingResult).hasErrors();
        verifyNoInteractions(productService);
    }

    @Test
    void deleteProductSuccess() {
        Integer productId = 1;

        ResponseEntity<Void> response = productController.deleteProduct(productId);

        assertEquals(ResponseEntity.noContent().build(), response);
        verify(productService).deleteProductById(productId);
    }

    @Test
    void deleteProductThrowsException() {
        Integer productId = 1;
        doThrow(new IllegalArgumentException("ProductNotFound"))
                .when(productService).deleteProductById(productId);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                productController.deleteProduct(productId));
        assertEquals("ProductNotFound", exception.getMessage());
        verify(productService).deleteProductById(productId);
    }
}
