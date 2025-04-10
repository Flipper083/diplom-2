package org.example.api;

import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import org.example.config.ConfigStellarBurgers;
import org.example.entity.Order;

import static io.restassured.RestAssured.given;

public class OrderClient extends ConfigStellarBurgers {
    // Путь для получения заказов
    private static final String PATH = "api/orders";
    // Путь для получения ингредиентов
    private static final String PATH_INGREDIENTS = "api/ingredients";

    // Метод для получения всех ингредиентов
    @Step("get all ingredients")
    public ValidatableResponse getAllIngredients() {
        return given()
                .spec(getSpec())
                .log().all() // Логируем запрос
                .get(PATH_INGREDIENTS) // Отправляем GET запрос по пути для ингредиентов
                .then()
                .log().all(); // Логируем ответ
    }

    // Метод для получения заказов с авторизацией
    @Step("get orders by authorization")
    public ValidatableResponse getOrdersByAuthorization(String accessToken) {
        return given()
                .spec(getSpec())
                .header("Authorization", accessToken)
                .log().all() // Логируем запрос
                .get(PATH) // Отправляем GET запрос по пути для заказов
                .then()
                .log().all(); // Логируем ответ
    }

    // Метод для получения заказов без авторизации
    @Step("get orders without authorization")
    public ValidatableResponse getOrdersWithoutAuthorization() {
        return given()
                .spec(getSpec())
                .log().all() // Логируем запрос
                .get(PATH) // Отправляем GET запрос по пути для заказов
                .then()
                .log().all(); // Логируем ответ
    }

    // Метод для создания заказа с авторизацией
    @Step("create order by authorization")
    public ValidatableResponse createOrderByAuthorization(Order order, String accessToken) {
        return given()
                .spec(getSpec())
                .header("Authorization", accessToken)
                .body(order) // В теле запроса передаем данные о заказе
                .log().all() // Логируем запрос
                .post(PATH) // Отправляем POST запрос для создания заказа
                .then()
                .log().all(); // Логируем ответ
    }

    // Метод для создания заказа без авторизации
    @Step("create order without authorization")
    public ValidatableResponse createOrderWithoutAuthorization(Order order) {
        return given()
                .spec(getSpec())
                .body(order) // В теле запроса передаем данные о заказе
                .log().all() // Логируем запрос
                .post(PATH) // Отправляем POST запрос для создания заказа
                .then()
                .log().all(); // Логируем ответ
    }
}