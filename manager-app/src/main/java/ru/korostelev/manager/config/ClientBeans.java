package ru.korostelev.manager.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.web.client.RestClient;
import ru.korostelev.manager.client.ProductsRestClientImp;
import org.springframework.beans.factory.annotation.Value;
import ru.korostelev.manager.security.OAuthClientHttpRequestInterceptor;


@Configuration
public class ClientBeans {

    @Bean
    public ProductsRestClientImp productsRestClient(
            @Value("${shop.services.catalogue.uri:http://localhost:8081}") String catalogueBaseUri,
            ClientRegistrationRepository clientRegistrationRepository,
            OAuth2AuthorizedClientRepository authorizedClientRepository,
            @Value("${shop.services.catalogue.registration-id:keycloak}") String registrationId) {
        return new ProductsRestClientImp(RestClient.builder()
                .baseUrl(catalogueBaseUri)
                .requestInterceptor(
                        new OAuthClientHttpRequestInterceptor(
                                new DefaultOAuth2AuthorizedClientManager(clientRegistrationRepository,
                                        authorizedClientRepository), registrationId))
                .build());
    }
}
