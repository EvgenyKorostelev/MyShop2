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
import ru.korostelev.catalogue.controller.payload.NewProductPayload;
import ru.korostelev.catalogue.entity.Product;
import ru.korostelev.catalogue.service.ProductService;

import java.net.URI;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Модульные тесты ProductsRestController")
public class ProductsRestControllerTest {


    @Mock
    private ProductService productService;

    @Mock
    private BindingResult bindingResult;

    @Mock
    private UriComponentsBuilder uriComponentsBuilder;

    @InjectMocks
    private ProductsRestController productsController;


    @Test
    @DisplayName("findProducts найдет и вернет список отфильтрованных товаров")
    void findProductsWithFilter() {
        String filter = "Molotok";
        List<Product> expectedProducts = List.of(
                new Product(1, "Molotok", "Bolshoy"));
        when(productService.findAllProducts(filter)).thenReturn(expectedProducts);

        List<Product> result = productsController.findProducts(filter);

        assertEquals(expectedProducts, result);
        verify(productService).findAllProducts(filter);
    }

    @Test
    @DisplayName("findProducts найдет и вернет список всех товаров")
    void findProductsWithoutFilter() {
        String filter = null;
        List<Product> expectedProducts = List.of(
                new Product(1, "Molotok", "Bolshoy"),
                new Product(2, "Mych", "Krugliy"));
        when(productService.findAllProducts(filter)).thenReturn(expectedProducts);

        List<Product> result = productsController.findProducts(filter);

        assertEquals(expectedProducts, result);
        verify(productService).findAllProducts(filter);
    }

    @Test
    @DisplayName("createProduct создаст товар и перенаправит на страницу созданного товара")
    void createProductSuccess() throws BindException {
        uriComponentsBuilder = UriComponentsBuilder.newInstance();
        NewProductPayload payload = new NewProductPayload("Molotok", "Bolshoy");
        Product product = new Product(1, "Molotok", "Bolshoy");
        when(bindingResult.hasErrors()).thenReturn(false);
        when(productService.createProduct(payload.title(), payload.description())).thenReturn(product);

        ResponseEntity<?> response = productsController.createProduct(
                payload, bindingResult, uriComponentsBuilder);

        assertEquals(ResponseEntity.created(
                URI.create("/catalogue-api/products/1")).body(product), response);
        verify(bindingResult).hasErrors();
        verify(productService).createProduct(payload.title(), payload.description());
    }

    @Test
    @DisplayName("createProduct вернет ошибку, bindingResult не BindingException")
    void createProductBindingResultErrors() {
        NewProductPayload payload = new NewProductPayload("", "");
        when(bindingResult.hasErrors()).thenReturn(true);

        assertThrows(BindException.class, () ->
                productsController.createProduct(payload, bindingResult, uriComponentsBuilder));
        verify(bindingResult).hasErrors();
        verifyNoInteractions(productService);
    }

    @Test
    @DisplayName("createProduct вернет ошибку, bindingResult является BindingException")
    void createProductBindExceptionFromBindingResult() {
        NewProductPayload payload = new NewProductPayload("", "");
        BindException exception = new BindException(bindingResult);
        when(bindingResult.hasErrors()).thenReturn(true);

        BindException thrown = assertThrows(BindException.class, () ->
                productsController.createProduct(payload, bindingResult, uriComponentsBuilder));
        assertEquals(exception.getMessage(), thrown.getMessage());
        verify(bindingResult).hasErrors();
        verifyNoInteractions(productService);
    }
}
