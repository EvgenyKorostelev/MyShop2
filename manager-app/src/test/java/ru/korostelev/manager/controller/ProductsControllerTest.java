package ru.korostelev.manager.controller;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.ConcurrentModel;
import ru.korostelev.manager.client.BadRequestException;
import ru.korostelev.manager.client.ProductsRestClient;
import ru.korostelev.manager.controller.payload.NewProductPayload;
import ru.korostelev.manager.entity.Product;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Модульные тесты ProductsController")
class ProductsControllerTest {

    @Mock
    ProductsRestClient productsRestClient;

    @InjectMocks
    ProductsController productsController;

//    @Test
//    @DisplayName("getProductsList вернет страницу со списком товаров")
//    void getProductsListTest(){
//        var model = new ConcurrentModel();
//
//        List<Product> expectedtList = new ArrayList<>(List.of(
//                new Product(1, "Myach", "krugliy"),
//                new Product(2, "Shar", "ovalniy")));
//
//        String result = productsController.getProductsList(model);
//        assertEquals("products/list", result);
//        verify(productsRestClient).findAllProducts();
//    }

    @Test
    @DisplayName("createProduct создаст новый товар и перенаправит на страницу товара")
    void createProductValidTest() {

        var payload = new NewProductPayload("Krug", "Mikhael");
        var model = new ConcurrentModel();

        doReturn(new Product(1, "Krug", "Mikhael"))
                .when(this.productsRestClient)
                .createProduct("Krug", "Mikhael");

        var result = this.productsController.createProduct(payload, model);
        assertEquals("redirect:/products/1", result);

        verify(this.productsRestClient).createProduct("Krug", "Mikhael");
        verifyNoMoreInteractions(this.productsRestClient);
    }

    @Test
    @DisplayName("createProduct вернет страницу с ошибками")
    void createProductInvalidTest() {

        var payload = new NewProductPayload("  ", "null");
        var model = new ConcurrentModel();

        doThrow(new BadRequestException(List.of("Ошибка 1", "Ошибка 2")))
                .when(this.productsRestClient)
                .createProduct("  ", "null");

        var result = this.productsController.createProduct(payload, model);
        assertEquals("products/product_new", result);
        assertEquals(payload, model.getAttribute("payload"));
        assertEquals(List.of("Ошибка 1", "Ошибка 2"), model.getAttribute("errors"));

        verify(this.productsRestClient).createProduct("  ", "null");
        verifyNoMoreInteractions(this.productsRestClient);
    }
}
