MyShop2 — это учебный проект веб-приложения маркетплейса,
реализованный на Java с использованием микросервисной архитектуры
и фреймворка Spring Boot. Приложение включает функционал для управления
товарами, взаимодействия с пользователями, обработки оценок и отзывов добавление товаров в корзину.

## Используемые технологии

* Java 21 — основной язык программирования.
* Spring Boot — для построения микросервисов, реализации REST API, защиты приложения.
* PostgreSQL — реляционная база данных для хранения данных о товарах и отзывах.
* Keycloak — сервер для хранения пользователй, аутентификации и авторизации.
* Docker — для контейнеризации и упрощения развертывания.
* JUnit и Mockito — для модульного тестирования бизнес-логики.

## Архитектура

Проект построен на микросервисной архитектуре, что позволяет каждому
модулю работать независимо и масштабироваться по мере роста нагрузки.

## Основные микросервисы включают:

* manager-app - приложение менеджера для управления товарами, расположено по адресу  http://localhost:8083.
* catalogue-service — сервис для приложения менеджера: добавление, редактирование,
  удаление и просмотр товаров, занимает порт http://localhost:8081.
* customer-app - приложение покупателя для просмотра каталога товаров, оценки,
  написания отзывов и добавления в корзину, расположено по адресу http://localhost:8084.
* feedback-service - сервис для приложения покупателя: добавление, редактирование,
  удаление и просмотр отзывов и оценок товаров, занимает порт http://localhost:8085.
* basket-service — сервис для приложения покупателя: управление корзиной,
  добавление удаление товаров., занимает порт http://localhost:8086
* eureka-server - сервис для регистрации модулей, занимает порт http://localhost:8761.
* keycloak - отдельный сервер подключаемый к приложению для аутентификации и авторизации
  пользователей и модулей, занимает порт http://localhost:8082.

## Базы данных:

* база данных товаров, занимает порт http://localhost:5432.
* база данных оценок и отзывов, занимает порт http://localhost:5434.

## Установка и запуск

1. ### Скачать данный репозиторий на машину, для запуска.
2. ### Поднять контейнеры с базами данных и сервером Keycloak.
   ### Контейнеры:
   #### Создание контейнера с базой товаров через докер, catalogue
    ```
    docker run --name catalogue-db -p 5432:5432 -e POSTGRES_DB=catalogue -e POSTGRES_USER=catalogue
    -e POSTGRES_PASSWORD=catalogue postgres:16
    ```

   #### Создание контейнера с базой отзывов через докер, feedback
    ```
    docker run --name feedback-db -p 5434:5432 -e POSTGRES_DB=feedback -e POSTGRES_USER=feedback
    -e POSTGRES_PASSWORD=feedback postgres:16
     ```

   #### Создание контейнера с сервером доступа через докер, keycloak
    ```
    docker run --name shop2-keycloak -p 8082:8080 -e KEYCLOAK_ADMIN=admin -e KEYCLOAK_ADMIN_PASSWORD=admin
    -v d:/MyShop2/config/keycloak/import:/opt/keycloak/data/import quay.io/keycloak/keycloak:26.0.0 start-dev
    --import-realm
    ```
   Настройки для keycloak лежат в папке ```D:\MyShop2\config\keycloak\import``` путь зависит от пути в контейнере.
3. ### Создать таблицы в базах данных
   #### Схема базы данных товаров
    ```
    create table catalogue.t_product(
    id            serial primary key,
    c_title varchar(50) not null check (length(trim(c_title)) >= 3),
    c_description varchar(1000)
    );
    ```
   #### Схема базы данных оценок отзывов
    ```
    CREATE TABLE if not exists feedback.t_favourite (
        id uuid NOT NULL,
    	c_productid int NOT NULL,
    	CONSTRAINT t_favourite_pk PRIMARY KEY (id)
        );
    
    CREATE TABLE if not exists feedback.t_review (
    	id uuid NOT NULL,
    	c_productid int NOT NULL,
    	c_rating int NOT NULL check (c_rating >= 1 AND c_rating <= 5),
    	c_review varchar(1000) NULL,
    	CONSTRAINT t_review_pk PRIMARY KEY (id)
    );
    ```
4. ### Запустить все контейнеры после запустить все модули приложения
5. ### Создание пользователей
    * Зайти в административную панель Keycloak по адресу http://localhost:8082
      логин: admin пароль: admin (или как указано при создании контейнера)
    * Создать realm MyShop2
    * Открыть вкладку Realm settings меню Configure
    * В правом верхнем углу раскрывающийся список Action -> Partial import выбрать файл с настройками конфигурации
      Keycloak, путь к файлу (относительно проекта) ```MyShop2\config\keycloak\import```
    * Открыть реалм MyShop2
    * Создать пользователя
    * Назначить ему группу Managers
    * Пользователя покупатель можно создать аналогичным образом, назначив ему группу Customers
      или на странице аутентификации по адресу http://localhost:8084
6. ### Приложение готово к использованию
    * порт приложения менеджера http://localhost:8083
    * порт приложения покупателя http://localhost:8084

## Документация rest сервисов
* Документация для catalogue-service находится по адресу http://localhost:8081/swagger-ui.html
* Документация для feedback-service находится по адресу http://localhost:8085/swagger-ui.html
* Документация для basket-service находится по адресу http://localhost:8086/swagger-ui.html

## Планы по развитию

* Создание системы оформления и оплаты заказов.
* Добавление более детализированных фильтров и поиска по товарам для удобства работы с большим каталогом.
* Оптимизация производительности с использованием кэширования.
* Поддержка асинхронных процессов для улучшения пользовательского опыта.
