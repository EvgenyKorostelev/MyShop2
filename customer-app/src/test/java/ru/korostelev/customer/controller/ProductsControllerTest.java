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
import ru.korostelev.customer.client.ProductsClient;
import ru.korostelev.customer.entity.BasketProduct;
import ru.korostelev.customer.entity.FavouriteProduct;
import ru.korostelev.customer.entity.Product;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Модульные тесты ProductsController")
public class ProductsControllerTest {

    @Mock
    private ProductsClient productsClient;

    @Mock
    private BasketClient basketClient;

    @Mock
    private FavouriteProductsClient favouriteProductsClient;

    @Mock
    private Model model;

    @InjectMocks
    private ProductsController productsController;

    @Test
    @DisplayName("getProductsListPage вернет страницу со списком отфильтрованных товаров")
    void getProductsListPageWithFilter() {
        String filter = "Molotok";
        Product product = new Product(1, "Molotok", "Bolshoy");
        BasketProduct basketProduct = new BasketProduct(1, 1);
        List<BasketProduct> basketProducts = List.of(basketProduct);
        List<Product> filteredProducts = List.of(product);
        when(productsClient.findAllProducts(filter)).thenReturn(filteredProducts);
        when(basketClient.findProductsInBasket()).thenReturn(basketProducts);

        String viewName = productsController.getProductsListPage(model, filter);

        assertEquals("customer/products/list", viewName);
        verify(model).addAttribute("filter", filter);
        verify(model).addAttribute("products", filteredProducts);
        verify(model).addAttribute("basket", 1);
        verify(productsClient).findAllProducts(filter);
        verify(basketClient).findProductsInBasket();
    }

    @Test
    @DisplayName("getProductsListPage вернет страницу со списком товаров без фильтра")
    void getProductsListPageWithoutFilter() {
        String filter = null;
        Product product1 = new Product(1, "Molotok", "Bolshoy");
        Product product2 = new Product(2, "Myach", "Krugliy");
        Product product3 = new Product(3, "Topor", "Ostriy");
        List<Product> allProducts = List.of(product1, product2, product3);
        when(productsClient.findAllProducts(filter)).thenReturn(allProducts);
        when(basketClient.findProductsInBasket()).thenReturn(Collections.emptyList());

        String viewName = productsController.getProductsListPage(model, filter);

        assertEquals("customer/products/list", viewName);
        verify(model).addAttribute("filter", filter);
        verify(model).addAttribute("products", allProducts);
        verify(model).addAttribute("basket", 0);
        verify(productsClient).findAllProducts(filter);
        verify(basketClient).findProductsInBasket();
    }

    @Test
    @DisplayName("getFavouriteProductsPage вернет страницу отфильтрованных избранных товаров")
    void getFavouriteProductsPageWithFilter() {
        String filter = "Molotok";
        List<FavouriteProduct> favouriteProducts = List.of(
                new FavouriteProduct(UUID.randomUUID(), 1),
                new FavouriteProduct(UUID.randomUUID(), 3));
        List<Product> filteredProducts = List.of(
                new Product(1, "Molotok", "Bolshoy"));
        List<BasketProduct> basketProducts = List.of(new BasketProduct(1, 1));
        when(favouriteProductsClient.findFavouriteProducts()).thenReturn(favouriteProducts);
        when(productsClient.findAllProducts(filter)).thenReturn(filteredProducts);
        when(basketClient.findProductsInBasket()).thenReturn(basketProducts);

        String viewName = productsController.getFavouriteProductsPage(model, filter);

        assertEquals("customer/products/favourites", viewName);
        verify(model).addAttribute("filter", filter);
        verify(model).addAttribute("products", filteredProducts);
        verify(model).addAttribute("basket", 1);
        verify(favouriteProductsClient).findFavouriteProducts();
        verify(productsClient, atLeastOnce()).findAllProducts(filter);
        verify(basketClient).findProductsInBasket();
    }

    @Test
    @DisplayName("getFavouriteProductsPage вернет страницу избранных товаров без фильтра")
    void getFavouriteProductsPageWithoutFilter() {
        String filter = null;
        List<FavouriteProduct> favouriteProducts = List.of(
                new FavouriteProduct(UUID.randomUUID(), 1),
                new FavouriteProduct(UUID.randomUUID(), 3));
        List<Product> allProducts = List.of(
                new Product(1, "Molotok", "Bolshoy"),
                new Product(2, "Myach", "Krugliy"),
                new Product(3, "Topor", "Ostriy"));
        List<Product> foundFavouriteProducts = List.of(
                new Product(1, "Molotok", "Bolshoy"),
                new Product(3, "Topor", "Ostriy"));
        when(favouriteProductsClient.findFavouriteProducts()).thenReturn(favouriteProducts);
        when(productsClient.findAllProducts(filter)).thenReturn(allProducts);
        when(basketClient.findProductsInBasket()).thenReturn(List.of());

        String viewName = productsController.getFavouriteProductsPage(model, filter);

        assertEquals("customer/products/favourites", viewName);
        verify(model).addAttribute("filter", filter);
        verify(model).addAttribute("products", foundFavouriteProducts);
        verify(model).addAttribute("basket", 0);
        verify(favouriteProductsClient).findFavouriteProducts();
        verify(productsClient, atLeastOnce()).findAllProducts(filter);
        verify(basketClient).findProductsInBasket();
    }


    @Test
    @DisplayName("getBasketPage вернет страницу отфильтрованных товаров в корзине")
    void getBasketPageWithFilter() {
        String filter = "Molotok";
        List<BasketProduct> basketProducts = List.of(
                new BasketProduct(1, 1),
                new BasketProduct(2, 5));
        List<Product> filteredProducts = List.of(
                new Product(1, "Molotok", "Bolshoy"));
        when(basketClient.findProductsInBasket()).thenReturn(basketProducts);
        when(productsClient.findAllProducts(filter)).thenReturn(filteredProducts);

        String viewName = productsController.getBasketPage(model, filter);

        assertEquals("customer/products/basket", viewName);
        verify(model).addAttribute("filter", filter);
        verify(model).addAttribute("products", filteredProducts);
        verify(basketClient).findProductsInBasket();
        verify(productsClient, atLeastOnce()).findAllProducts(filter);
    }

    @Test
    @DisplayName("getBasketPage вернет страницу товаров в корзине без фильтра")
    void getBasketPageWithoutFilter() {
        String filter = null;
        List<BasketProduct> basketProducts = List.of(
                new BasketProduct(1, 1),
                new BasketProduct(2, 5));
        List<Product> allProducts = List.of(
                new Product(1, "Molotok", "Bolshoy"),
                new Product(2, "Myach", "Krugliy"),
                new Product(3, "Topor", "Ostriy"));
        List<Product> foundBasketProducts = List.of(
                new Product(1, "Molotok", "Bolshoy"),
                new Product(2, "Myach", "Krugliy"));
        when(basketClient.findProductsInBasket()).thenReturn(basketProducts);
        when(productsClient.findAllProducts(filter)).thenReturn(allProducts);

        String viewName = productsController.getBasketPage(model, filter);

        assertEquals("customer/products/basket", viewName);
        verify(model).addAttribute("filter", filter);
        verify(model).addAttribute("products", foundBasketProducts);
        verify(basketClient).findProductsInBasket();
        verify(productsClient, atLeastOnce()).findAllProducts(filter);
    }
}

