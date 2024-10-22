package ru.korostelev.customer.client;

import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import ru.korostelev.customer.client.exception.BadRequestException;
import ru.korostelev.customer.client.payload.NewFavouriteProductPayload;
import ru.korostelev.customer.entity.FavouriteProduct;

import java.util.List;
import java.util.NoSuchElementException;

@RequiredArgsConstructor
public class FavouriteProductsClientImp implements FavouriteProductsClient {

    private static final ParameterizedTypeReference<List<FavouriteProduct>> FAVOURITES_PRODUCTS_TYPE_REFERENCE =
            new ParameterizedTypeReference<>() {
            };

    private final RestClient restClient;

    @Override
    public List<FavouriteProduct> findFavouriteProducts() {
        return this.restClient
                .get()
                .uri("/feedback-api/favourite-products")
                .retrieve()
                .body(FAVOURITES_PRODUCTS_TYPE_REFERENCE);
    }

    @Override
    public FavouriteProduct findFavouriteProductByProductId(int id) {
        try {
            return this.restClient
                    .get()
                    .uri("/feedback-api/favourite-products/by-product-id/{id}", id)
                    .retrieve()
                    .body(FavouriteProduct.class);
        } catch (HttpClientErrorException.NotFound exception) {
            return null;
        }
    }

    @Override
    public FavouriteProduct addProductToFavourites(int id) {
        try {
            return this.restClient
                    .post()
                    .uri("/feedback-api/favourite-products")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new NewFavouriteProductPayload(id))
                    .retrieve()
                    .body(FavouriteProduct.class);
        } catch (HttpClientErrorException.BadRequest exception) {
            ProblemDetail problemDetail = exception.getResponseBodyAs(ProblemDetail.class);
            throw new BadRequestException((List<String>) problemDetail.getProperties().get("errors"));
        }
    }

    @Override
    public void removeProductFromFavourites(int id) {
        try {
            this.restClient
                    .delete()
                    .uri("/feedback-api/favourite-products/by-product-id/{id}", id)
                    .retrieve()
                    .toBodilessEntity();
        } catch (HttpClientErrorException.NotFound exception) {
            throw new NoSuchElementException(exception);
        }
    }
}
