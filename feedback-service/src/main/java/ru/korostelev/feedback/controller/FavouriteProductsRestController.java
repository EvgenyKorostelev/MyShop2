package ru.korostelev.feedback.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import ru.korostelev.feedback.controller.payload.NewFavouriteProductPayload;
import ru.korostelev.feedback.entity.FavouriteProduct;
import ru.korostelev.feedback.service.FavouriteProductsService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("feedback-api/favourite-products")
@RequiredArgsConstructor
public class FavouriteProductsRestController {

    private final FavouriteProductsService favouriteProductsService;

    @GetMapping
    @Operation(security = @SecurityRequirement(name = "keycloak"))
    public List<FavouriteProduct> findFavouriteProducts() {
        return this.favouriteProductsService.findFavouriteProducts();
    }

    @GetMapping("by-product-id/{id:\\d+}")
    @Operation(security = @SecurityRequirement(name = "keycloak"))
    public FavouriteProduct findFavouriteProductByProductId(@PathVariable("id") int id) {
        return this.favouriteProductsService.findFavouriteProductByProduct(id);
    }

    @PostMapping
    @Operation(security = @SecurityRequirement(name = "keycloak"))
    public ResponseEntity<FavouriteProduct> addProductToFavourites(
            @Valid @RequestBody NewFavouriteProductPayload payload,
            BindingResult bindingResult,
            UriComponentsBuilder uriComponentsBuilder) throws BindException {
        if (bindingResult.hasErrors()) {
            if (bindingResult instanceof BindException exception) {
                throw exception;
            } else {
                throw new BindException(bindingResult);
            }
        } else {
            FavouriteProduct favouriteProduct = this.favouriteProductsService.addProductToFavourites(payload.id());
            return ResponseEntity
                    .created(uriComponentsBuilder
                            .replacePath("feedback-api/favourite-products/{uuid}")
                            .build(Map.of("uuid", favouriteProduct.getUuid())))
                            .body(favouriteProduct);
        }
    }

    @DeleteMapping("by-product-id/{id:\\d+}")
    @Operation(security = @SecurityRequirement(name = "keycloak"))
    public ResponseEntity<Void> removeProductFromFavourites(@PathVariable("id") int id) {
        this.favouriteProductsService.removeProductFromFavourites(id);
        return ResponseEntity.noContent().build();
    }
}
