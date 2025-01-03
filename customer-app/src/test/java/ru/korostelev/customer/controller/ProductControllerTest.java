package ru.korostelev.customer.controller;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;
import ru.korostelev.customer.client.BasketClient;
import ru.korostelev.customer.client.ProductsClient;

@ExtendWith(MockitoExtension.class)
@DisplayName("Модульные тесты ProductController")
public class ProductControllerTest {

    @Mock
    private ProductsClient productsClient;

    @Mock
    private BasketClient basketClient;

    @Mock
    private Model model;

    @InjectMocks
    private ProductsController productsController;
}
