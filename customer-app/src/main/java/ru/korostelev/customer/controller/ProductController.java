package ru.korostelev.customer.controller;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.korostelev.customer.client.FavouriteProductsClient;
import ru.korostelev.customer.client.ProductReviewsClient;
import ru.korostelev.customer.client.ProductsClient;
import ru.korostelev.customer.client.exception.BadRequestException;
import ru.korostelev.customer.controller.payload.NewProductReviewPayload;
import ru.korostelev.customer.entity.Product;
import ru.korostelev.customer.entity.ProductReview;

import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
@RequestMapping("customer/products/{id:\\d+}")
public class ProductController {

    private final ProductsClient productsClient;

    private final FavouriteProductsClient favouriteProductsClient;

    private final ProductReviewsClient productReviewsClient;

    private final MessageSource messageSource;

    @ModelAttribute(name = "product", binding = false)
    public Product product(@PathVariable("id") int id) {
        return Optional.of(this.productsClient.findProduct(id))
                .orElseThrow(() -> new NoSuchElementException("customer.products.error.not_found"));
    }

    @GetMapping
    public String getProduct(@PathVariable("id") int id, Model model) {
        model.addAttribute("inFavourite", false);
        model.addAttribute("reviews", this.productReviewsClient.findProductReviewsByProductId(id));
        if (favouriteProductsClient.findFavouriteProductByProductId(id) != null) {
            model.addAttribute("inFavourite", true);
        }
        return "customer/products/product";
    }

    @PostMapping("add-to-favourites")
    public String addProductToFavourites(@ModelAttribute("product") Product product,
                                         Model model) {
        try {
            this.favouriteProductsClient.addProductToFavourites(product.id());
            return "redirect:/customer/products/%d".formatted(product.id());
        } catch (BadRequestException exception) {
            model.addAttribute("errors", exception.getErrors());
            return "redirect:/customer/products/%d".formatted(product.id());
        }
    }

    @PostMapping("remove-from-favourites")
    public String removeProductFromFavourites(@ModelAttribute("product") Product product) {
        this.favouriteProductsClient.removeProductFromFavourites(product.id());
        return "redirect:/customer/products/%d".formatted(product.id());
    }

    @PostMapping("create-review")
    public String createReview(@ModelAttribute("product") Product product,
                               NewProductReviewPayload payload,
                               Model model) {
        try {
            this.productReviewsClient.createProductReview(product.id(), payload.rating(), payload.review());
            return "redirect:/customer/products/%d".formatted(product.id());
        } catch (BadRequestException exception) {
            model.addAttribute("inFavourite", false);
            model.addAttribute("payload", payload);
            model.addAttribute("errors", exception.getErrors());
            this.favouriteProductsClient.findFavouriteProductByProductId(product.id());
            model.addAttribute("inFavourite", true);
            return "customer/products/product";
        }
    }

    @ExceptionHandler(NoSuchElementException.class)
    public String handleNoSuchElementException(NoSuchElementException exception, Model model,
                                               HttpServletResponse response, Locale locale) {
        response.setStatus(HttpStatus.NOT_FOUND.value());
        model.addAttribute("error",
                this.messageSource.getMessage(exception.getMessage(), new Object[0],
                        exception.getMessage(), locale));
        return "errors/404";
    }
}
