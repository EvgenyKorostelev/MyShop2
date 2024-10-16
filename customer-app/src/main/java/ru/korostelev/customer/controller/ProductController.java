package ru.korostelev.customer.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.korostelev.customer.client.ProductsRestClient;
import ru.korostelev.customer.controller.payload.NewProductReviewPayload;
import ru.korostelev.customer.entity.Product;
import ru.korostelev.customer.service.FavouriteProductsService;
import ru.korostelev.customer.service.ProductReviewsService;

import java.util.NoSuchElementException;

@Controller
@RequiredArgsConstructor
@RequestMapping("customer/products/{id:\\d+}")
public class ProductController {

    private final ProductsRestClient productsRestClient;

    private final FavouriteProductsService favouriteProductsService;

    private final ProductReviewsService productReviewsService;

    @ModelAttribute(name = "product", binding = false)
    public Product loadProduct(@PathVariable("id") int id) {
        return this.productsRestClient.findProduct(id)
                .switchIfEmpty(Mono.error(new NoSuchElementException("customer.products.error.not_found")));
    }

    @GetMapping
    public String getProduct(@PathVariable("id") int id, Model model) {
        model.addAttribute("inFavourite", false);
        return this.productReviewsService.findProductReviewsByProduct(id)
                .collectList()
                .doOnNext(productReviews -> model.addAttribute("reviews", productReviews))
                .then(this.favouriteProductsService.findFavouriteProductByProduct(id)
                        .doOnNext(favouriteProduct -> model.addAttribute("inFavourite", true)))
                .thenReturn("customer/products/product");
    }

    @PostMapping("add-to-favourites")
    public String addProductToFavourites(@ModelAttribute("product") Product product) {
        return product
                .map(Product::id)
                .flatMap(id -> this.favouriteProductsService.addProductToFavourites(id)
                        .thenReturn("redirect:/customer/products/%d".formatted(id)));
    }

    @PostMapping("remove-from-favourites")
    public String removeProductFromFavourites(@ModelAttribute("product") Product product) {
        return product
                .map(Product::id)
                .flatMap(id -> this.favouriteProductsService.removeProductFromFavourites(id)
                        .thenReturn("redirect:/customer/products/%d".formatted(id)));
    }

    @PostMapping("create-review")
    public String createReview(@PathVariable("id") int id,
                                     @Valid NewProductReviewPayload payload,
                                     BindingResult bindingResult,
                                     Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("inFavourite", false);
            model.addAttribute("payload", payload);
            model.addAttribute("errors", bindingResult.getAllErrors().stream()
                    .map(ObjectError::getDefaultMessage)
                    .toList());
            return this.favouriteProductsService.findFavouriteProductByProduct(id)
                    .doOnNext(favouriteProduct -> model.addAttribute("inFavourite", true))
                    .thenReturn("customer/products/product");
        } else {
            return this.productReviewsService.createProductReview(id, payload.rating(), payload.review())
                    .thenReturn("redirect:/customer/products/%d".formatted(id));
        }
    }

    @ExceptionHandler(NoSuchElementException.class)
    public String handleNoSuchElementException(NoSuchElementException exception, Model model) {
        model.addAttribute("error", exception.getMessage());
        return "errors/404";
    }
}
