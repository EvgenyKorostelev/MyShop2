package ru.korostelev.customer.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.korostelev.customer.client.ProductsRestClient;
import ru.korostelev.customer.service.FavouriteProductsService;

@Controller
@RequiredArgsConstructor
@RequestMapping("customer/products")
public class ProductsController {

    private final ProductsRestClient productsRestClient;

    private final FavouriteProductsService favouriteProductsService;

    @GetMapping("list")
    public String getProductsList(Model model,
                                  @RequestParam(name = "filter", required = false) String filter){
        model.addAttribute("filter", filter);
        return this.productsRestClient.findAllProducts(filter)
                .collectList()
                .doOnNext(products -> model.addAttribute("products", products))
                .thenReturn("customer/products/list");
    }

    @GetMapping("favourites")
    public String getFavouritesProducts(Model model,
                                              @RequestParam(name = "filter", required = false) String filter){
        model.addAttribute("filter", filter);
        return this.favouriteProductsService.findFavouriteProducts()
                .map(FavouriteProduct::getProductId)
                .collectList()
                .flatMap(favouriteProducts -> this.productsClient.findAllProducts(filter)
                        .filter(product -> favouriteProducts.contains(product.id()))
                        .collectList()
                        .doOnNext(products -> model.addAttribute("products", products)))
                .thenReturn("customer/products/favourites");
    }
}
