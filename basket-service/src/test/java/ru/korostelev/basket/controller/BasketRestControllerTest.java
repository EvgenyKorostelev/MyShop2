package ru.korostelev.basket.controller;


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
import ru.korostelev.basket.controller.payload.NewProductPayload;
import ru.korostelev.basket.entity.Product;
import ru.korostelev.basket.service.BasketService;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Модульные тесты BasketRestController")
public class BasketRestControllerTest {

    @Mock
    private BasketService basketService;

    @Mock
    private BindingResult bindingResult;

    @Mock
    private UriComponentsBuilder uriComponentsBuilder;

    @InjectMocks
    private BasketRestController basketController;

    @Test
    void addProductToBasketSuccess() throws BindException {
        uriComponentsBuilder = UriComponentsBuilder.newInstance();
        NewProductPayload payload = new NewProductPayload(1, 5);
        Product product = new Product(1,1);
        when(bindingResult.hasErrors()).thenReturn(false);
        when(basketService.addToBasket(payload.id(), payload.count())).thenReturn(product);

        ResponseEntity<Product> response =
                basketController.addProductToBasket(payload, bindingResult, uriComponentsBuilder);

        URI expectedUri = URI.create("basket-api/added-products/1");
        assertEquals(ResponseEntity.created(expectedUri).body(product), response);
        verify(bindingResult).hasErrors();
        verify(basketService).addToBasket(payload.id(), payload.count());
    }

    @Test
    void addProductToBasketBindingResultErrors() {
        NewProductPayload payload = new NewProductPayload(1, 5);
        when(bindingResult.hasErrors()).thenReturn(true);

        assertThrows(BindException.class, () ->
                basketController.addProductToBasket(payload, bindingResult, uriComponentsBuilder));
        verify(bindingResult).hasErrors();
        verifyNoInteractions(basketService);
    }

    @Test
    void addProductToBasketBindExceptionFromBindingResult() {
        NewProductPayload payload = new NewProductPayload(1, 5);
        BindException exception = new BindException(bindingResult);
        when(bindingResult.hasErrors()).thenReturn(true);

        BindException thrown = assertThrows(BindException.class, () ->
                basketController.addProductToBasket(payload, bindingResult, uriComponentsBuilder));
        assertEquals(exception.getMessage(), thrown.getMessage());
        verify(bindingResult).hasErrors();
        verifyNoInteractions(basketService);
    }

}
