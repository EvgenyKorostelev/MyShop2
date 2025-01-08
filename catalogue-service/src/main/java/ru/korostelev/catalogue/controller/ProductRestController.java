package ru.korostelev.catalogue.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.event.Level;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.korostelev.catalogue.aspect.Logger;
import ru.korostelev.catalogue.controller.payload.UpdateProductPayload;
import ru.korostelev.catalogue.entity.Product;
import ru.korostelev.catalogue.service.ProductService;

import java.util.Locale;
import java.util.NoSuchElementException;

@Logger(level = Level.INFO)
@RestController
@RequestMapping("catalogue-api/products/{id:\\d+}")
@RequiredArgsConstructor
public class ProductRestController {

    private final ProductService productService;

    private final MessageSource messageSource;

    @ModelAttribute("product")
    public Product getProduct(@PathVariable("id") Integer id) {
        return this.productService.findProductById(id)
                .orElseThrow(() -> new NoSuchElementException("catalogue.errors.product.not_found"));
    }

    @GetMapping
    @Operation(security = @SecurityRequirement(name = "keycloak"))
    public Product findProduct(@ModelAttribute("product") Product product) {
        return product;
    }

    @PatchMapping
    @Operation(security = @SecurityRequirement(name = "keycloak"))
    public ResponseEntity<?> updateProduct(@PathVariable("id") Integer id,
                                           @Valid @RequestBody UpdateProductPayload payload,
                                           BindingResult bindingResult)
            throws BindException {
        if (bindingResult.hasErrors()) {
            if (bindingResult instanceof BindException exception) {
                throw exception;
            } else {
                throw new BindException(bindingResult);
            }
        } else {
            this.productService.updateProduct(id, payload.title(), payload.description());
            return ResponseEntity.noContent()
                    .build();
        }
    }

    @DeleteMapping
    @Operation(security = @SecurityRequirement(name = "keycloak"))
    public ResponseEntity<Void> deleteProduct(@PathVariable("id") Integer id) {
        this.productService.deleteProductById(id);
        return ResponseEntity.noContent()
                .build();
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ProblemDetail> handleNoSuchElementException(NoSuchElementException exception,
                                                                      Locale locale) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND,
                        this.messageSource.getMessage(exception.getMessage(), new Object[0],
                                exception.getMessage(), locale)));
    }
}
