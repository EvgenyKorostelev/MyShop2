package ru.korostelev.customer.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.web.client.RestClient;
import ru.korostelev.customer.client.BasketClientImp;
import ru.korostelev.customer.client.FavouriteProductsClientImp;
import ru.korostelev.customer.client.ProductReviewsClientImp;
import ru.korostelev.customer.client.ProductsClientImp;
import ru.korostelev.customer.security.OAuthClientHttpRequestInterceptor;


@Configuration
public class ClientBeans {

    @Bean
    public ProductsClientImp productsRestClient(
            @Value("${shop.services.catalogue.uri:http://localhost:8081}") String catalogueBaseUri,
            ClientRegistrationRepository clientRegistrationRepository,
            OAuth2AuthorizedClientRepository authorizedClientRepository,
            @Value("${shop.services.feedback.registration-id:keycloak}") String registrationId) {
        return new ProductsClientImp(RestClient.builder()
                .baseUrl(catalogueBaseUri)
                .requestInterceptor(
                        new OAuthClientHttpRequestInterceptor(
                                new DefaultOAuth2AuthorizedClientManager(clientRegistrationRepository,
                                        authorizedClientRepository), registrationId))
                .build());
    }

    @Bean
    public FavouriteProductsClientImp favouriteProductsRestClient(
            @Value("${shop.services.feedback.uri:http://localhost:8085}") String favouriteBaseUri,
            ClientRegistrationRepository clientRegistrationRepository,
            OAuth2AuthorizedClientRepository authorizedClientRepository,
            @Value("${shop.services.feedback.registration-id:keycloak}") String registrationId) {
        return new FavouriteProductsClientImp(RestClient.builder()
                .baseUrl(favouriteBaseUri)
                .requestInterceptor(
                        new OAuthClientHttpRequestInterceptor(
                                new DefaultOAuth2AuthorizedClientManager(clientRegistrationRepository,
                                        authorizedClientRepository), registrationId))
                .build());
    }

    @Bean
    public ProductReviewsClientImp productReviewsRestClient(
            @Value("${shop.services.feedback.uri:http://localhost:8085}") String favouriteBaseUri,
            ClientRegistrationRepository clientRegistrationRepository,
            OAuth2AuthorizedClientRepository authorizedClientRepository,
            @Value("${shop.services.feedback.registration-id:keycloak}") String registrationId) {
        return new ProductReviewsClientImp(RestClient.builder()
                .baseUrl(favouriteBaseUri)
                .requestInterceptor(
                        new OAuthClientHttpRequestInterceptor(
                                new DefaultOAuth2AuthorizedClientManager(clientRegistrationRepository,
                                        authorizedClientRepository), registrationId))
                .build());
    }

    @Bean
    public BasketClientImp basketRestClient(
            @Value("${shop.services.basket.uri:http://localhost:8086}") String basketBaseUri,
            ClientRegistrationRepository clientRegistrationRepository,
            OAuth2AuthorizedClientRepository authorizedClientRepository,
            @Value("${shop.services.basket.registration-id:keycloak}") String registrationId) {
        return new BasketClientImp(RestClient.builder()
                .baseUrl(basketBaseUri)
                .requestInterceptor(
                        new OAuthClientHttpRequestInterceptor(
                                new DefaultOAuth2AuthorizedClientManager(clientRegistrationRepository,
                                        authorizedClientRepository), registrationId))
                .build());
    }
}
