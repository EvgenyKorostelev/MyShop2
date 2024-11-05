## Контейнеры
### Создание контейнера с базой товаров через докер, catalogue
```docker run --name catalogue-db -p 5432:5432 -e POSTGRES_DB=catalogue -e POSTGRES_USER=catalogue -e POSTGRES_PASSWORD=catalogue postgres:16```
### Создание контейнера с базой отзывов через докер, feedback
```docker run --name feedback-db -p 5434:5432 -e POSTGRES_DB=feedback -e POSTGRES_USER=feedback -e POSTGRES_PASSWORD=feedback postgres:16```
### Создание контейнера с сервером доступа через докер, keycloak
```docker run --name shop2-keycloak -p 8082:8080 -e KEYCLOAK_ADMIN=admin -e KEYCLOAK_ADMIN_PASSWORD=admin -v d:/MyShop2/config/keycloak/import:/opt/keycloak/data/import quay.io/keycloak/keycloak:26.0.0 start-dev --import-realm```
