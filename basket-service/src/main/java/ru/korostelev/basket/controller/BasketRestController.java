package ru.korostelev.basket.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import ru.korostelev.basket.controller.payload.NewProductPayload;
import ru.korostelev.basket.entity.Product;
import ru.korostelev.basket.service.BasketService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("basket-api/added-products")
@RequiredArgsConstructor
public class BasketRestController {

    private final BasketService basketService;

    @GetMapping
    public List<Product> productsInBasket(){
        return this.basketService.findProductsInBasket();
    }

    @GetMapping("by-product-id/{id:\\d+}")
    public Product getProductById(@PathVariable("id") int id){
        return this.basketService.findProductById(id);
    }

    @PostMapping
    public ResponseEntity<Product> addProductToBasket(@RequestBody NewProductPayload payload,
                                                      BindingResult bindingResult,
                                                      UriComponentsBuilder uriComponentsBuilder) throws BindException {
        if (bindingResult.hasErrors()) {
            if (bindingResult instanceof BindException exception) {
                throw exception;
            } else {
                throw new BindException(bindingResult);
            }
        } else {
            Product product = this.basketService.addToBasket(payload.id(), payload.count());
            return ResponseEntity
                    .created(uriComponentsBuilder
                            .replacePath("basket-api/added-products/{id}")
                            .build(Map.of("id", product.getId())))
                    .body(product);
        }
    }

    @DeleteMapping("by-product-id/{id:\\d+}")
    private ResponseEntity<Void> removeProductFromBasket(@PathVariable("id") int id) {
        this.basketService.deleteFromBasketById(id);
        return ResponseEntity.noContent().build();
    }
}
