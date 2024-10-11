package ru.korostelev.manager.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestClient;
import ru.korostelev.manager.client.ProductsRestClientImp;
import org.springframework.beans.factory.annotation.Value;


@Configuration
public class ClientBeans {

    @Bean
    public ProductsRestClientImp productsRestClient(
            @Value("${shop.catalogue.service.uri:http://localhost:8081}") String catalogueBaseUri) {
        return new ProductsRestClientImp(RestClient.builder()
                .baseUrl(catalogueBaseUri)
                .build());
    }
}
