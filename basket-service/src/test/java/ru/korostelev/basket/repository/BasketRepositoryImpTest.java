package ru.korostelev.basket.repository;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.korostelev.basket.entity.Product;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Модульные тесты BasketRepositoryImp")
public class BasketRepositoryImpTest {

    private BasketRepositoryImp basketRepository;

    @BeforeEach
    void setUp() {
        basketRepository = new BasketRepositoryImp();
        basketRepository.save(new Product(1, 100));
        basketRepository.save(new Product(2, 200));
        basketRepository.save(new Product(3, 300));
    }

    @Test
    void findAllProductsReturnsAllProducts() {
        List<Product> products = basketRepository.findAllProducts();

        assertNotNull(products);
        assertEquals(3, products.size());
    }

    @Test
    void findProductByIdReturnsProductWhenIdExists() {
        Product product = basketRepository.findProductById(2);

        assertNotNull(product);
        assertEquals(2, product.getId());
        assertEquals(200, product.getCount());
    }

    @Test
    void findProductByIdReturnsNullWhenIdDoesNotExist() {
        Product product = basketRepository.findProductById(99);

        assertNull(product);
    }

    @Test
    void saveAddsProductToRepository() {
        Product newProduct = new Product(4, 400);

        basketRepository.save(newProduct);
        List<Product> products = basketRepository.findAllProducts();

        assertEquals(4, products.size());
        assertTrue(products.contains(newProduct));
    }

    @Test
    void deleteByIdRemovesProductWhenIdExists() {
        basketRepository.deleteById(2);
        List<Product> products = basketRepository.findAllProducts();

        assertEquals(2, products.size());
        assertNull(basketRepository.findProductById(2));
    }

    @Test
    void deleteByIdDoesNothingWhenIdDoesNotExist() {
        basketRepository.deleteById(99);
        List<Product> products = basketRepository.findAllProducts();

        assertEquals(3, products.size());
    }

    @Test
    void findAllProductsIsThreadSafe() throws InterruptedException {
        List<Product> synchronizedProducts = basketRepository.findAllProducts();
        Thread writerThread = new Thread(() -> basketRepository.save(new Product(4, 400)));
        Thread readerThread = new Thread(() -> assertNotNull(basketRepository.findProductById(4)));

        writerThread.start();
        writerThread.join();
        readerThread.start();
        readerThread.join();

        assertEquals(4, synchronizedProducts.size());
    }
}
