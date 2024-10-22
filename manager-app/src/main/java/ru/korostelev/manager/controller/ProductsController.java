package ru.korostelev.manager.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.korostelev.manager.client.exception.BadRequestException;
import ru.korostelev.manager.client.ProductsRestClient;
import ru.korostelev.manager.controller.payload.NewProductPayload;
import ru.korostelev.manager.entity.Product;


@Controller
@RequiredArgsConstructor
@RequestMapping("products")
public class ProductsController {

    private final ProductsRestClient productsRestClient;

    @GetMapping("list")
    public String getProductsList(Model model, @RequestParam(name = "filter", required = false) String filter) {
        model.addAttribute("products", this.productsRestClient.findAllProducts(filter));
        model.addAttribute("filter", filter);
        return "products/list";
    }

    @GetMapping("create")
    public String getNewProduct() {
        return "products/product_new";
    }

    @PostMapping("create")
    public String createProduct(NewProductPayload payload,
                                Model model) {
        try {
            Product product = this.productsRestClient.createProduct(payload.title(), payload.description());
            return "redirect:/products/%d".formatted(product.id());
        } catch (BadRequestException exception) {
            model.addAttribute("payload", payload);
            model.addAttribute("errors", exception.getErrors());
            return "products/product_new";
        }
    }
}
