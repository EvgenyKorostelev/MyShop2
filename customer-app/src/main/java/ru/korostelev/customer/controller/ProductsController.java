package ru.korostelev.customer.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.korostelev.customer.client.BasketClient;
import ru.korostelev.customer.client.FavouriteProductsClient;
import ru.korostelev.customer.client.ProductsClient;
import ru.korostelev.customer.entity.BasketProduct;
import ru.korostelev.customer.entity.FavouriteProduct;
import ru.korostelev.customer.entity.Product;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("customer/products")
public class ProductsController {

    private final ProductsClient productsClient;

    private final FavouriteProductsClient favouriteProductsClient;

    private final BasketClient basketClient;

    @GetMapping("list")
    public String getProductsListPage(Model model, @RequestParam(name = "filter", required = false) String filter) {
        model.addAttribute("filter", filter);
        model.addAttribute("products", this.productsClient.findAllProducts(filter));
        model.addAttribute("basket", this.basketClient.findProductsInBasket().size());
        return "customer/products/list";
    }

    @GetMapping("favourites")
    public String getFavouriteProductsPage(Model model, @RequestParam(name = "filter", required = false) String filter) {
        model.addAttribute("filter", filter);
        List<Product> products = this.favouriteProductsClient.findFavouriteProducts().stream()
                .map(FavouriteProduct::id)
                .flatMap(favouriteProducts -> this.productsClient.findAllProducts(filter).stream()
                        .filter(product -> favouriteProducts.equals(product.id())))
                .toList();
        model.addAttribute("products", products);
        model.addAttribute("basket", this.basketClient.findProductsInBasket().size());
        return "customer/products/favourites";
    }

    @GetMapping("basket")
    public String getBasketPage(Model model, @RequestParam(name = "filter", required = false) String filter) {
        model.addAttribute("filter", filter);
        List<Product> products = this.basketClient.findProductsInBasket().stream()
                .map(BasketProduct::id)
                .flatMap(basketProducts -> this.productsClient.findAllProducts(filter).stream()
                        .filter(product -> basketProducts.equals(product.id())))
                .toList();
        model.addAttribute("products", products);
        return "customer/products/basket";
    }
}
