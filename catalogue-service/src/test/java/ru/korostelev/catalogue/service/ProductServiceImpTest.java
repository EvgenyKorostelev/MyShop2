package ru.korostelev.catalogue.service;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.korostelev.catalogue.entity.Product;
import ru.korostelev.catalogue.repository.ProductRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
@DisplayName("Модульные тесты ProductServiceImp")
class ProductServiceImpTest {

    @Mock
    ProductRepository productRepository;

    @InjectMocks
    ProductServiceImp productServiceImp;

    @Test
    @DisplayName("findAllProducts найдет и вернет список товаров")
    void findAllProductsTest() {
        String filter = null;
        List<Product> expectedtList = new ArrayList<>(List.of(
                new Product(1, "Myach", "krugliy"),
                new Product(2, "Shar", "ovalniy")));

        when(productRepository.findAll()).thenReturn(expectedtList);
        List<Product> actualProducts = productServiceImp.findAllProducts(filter);
        assertEquals(expectedtList, actualProducts);
    }

    @Test
    @DisplayName("createProduct создаст и вернет новый товар")
    void createProductTest() {

        String newTitlePayload = "Kub";
        String newDescriptionPayload = "kvadratniy";

        Product expectedProduct = new Product(null, newTitlePayload, newDescriptionPayload);

        when(productRepository.save(expectedProduct)).thenReturn(expectedProduct);
        Product actualProduct = productServiceImp.createProduct(
                expectedProduct.getTitle(), expectedProduct.getDescription());
        assertEquals(expectedProduct, actualProduct);
    }

    @Test
    @DisplayName("findProductById найдет и вернет товар по id")
    void findProductByIdTest() {

        Product expectedProduct = new Product(1, "Myach", "krugliy");

        when(productRepository.findById(expectedProduct.getId())).thenReturn(Optional.of(expectedProduct));
        Optional<Product> actualProduct = productServiceImp.findProductById(expectedProduct.getId());
        assertEquals(expectedProduct, actualProduct.get());
    }

    @Test
    @DisplayName("updateProduct обновит товар")
    void updateProductTest() {

        Product updatedProduct = new Product(1, "Myach", "krugliy");
        String updatedTitlePayload = "Kub";
        String updatedDescriptionPayload = "kvadratniy";

        when(productRepository.findById(updatedProduct.getId())).thenReturn(Optional.of(updatedProduct));
        Product expectedProduct = productServiceImp.findProductById(updatedProduct.getId()).get();
        expectedProduct.setTitle(updatedTitlePayload);
        expectedProduct.setDescription(updatedDescriptionPayload);
        productServiceImp.updateProduct(updatedProduct.getId(), updatedTitlePayload, updatedDescriptionPayload);
        Product actualProduct = productServiceImp.findProductById(updatedProduct.getId()).get();
        assertEquals(expectedProduct, actualProduct);
    }

    @Test
    @DisplayName("deleteProductById удалит товар")
    void deleteProductByIdTest() {

        Product expectedProduct = new Product(1, "Myach", "krugliy");

        productServiceImp.deleteProductById(expectedProduct.getId());
        verify(productRepository).deleteById(expectedProduct.getId());
    }
}
