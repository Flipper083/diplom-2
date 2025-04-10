package org.example.api;

import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import org.example.config.ConfigStellarBurgers;
import org.example.entity.Client;

import static io.restassured.RestAssured.given;

public class UserClient extends ConfigStellarBurgers {

    // Пути для различных операций с клиентом
    private static final String CREATE_CLIENT_PATH = "api/auth/register"; // Путь для регистрации клиента
    private static final String LOGIN_CLIENT_PATH = "api/auth/login"; // Путь для логина клиента
    private static final String LOGOUT_CLIENT_PATH = "api/auth/logout"; // Путь для логаута клиента
    private static final String DELETE_CLIENT_PATH = "api/auth/user"; // Путь для удаления клиента
    private static final String CLIENT_PATH = "api/auth/user"; // Путь для получения и обновления информации о клиенте

    // Метод для получения информации о клиенте по токену
    @Step("get client")
    public ValidatableResponse getClient(String accessToken) {
        return given()
                .spec(getSpec())
                .header("Authorization", accessToken)
                .log().all() // Логируем запрос
                .get(CLIENT_PATH) // Отправляем GET запрос по пути для получения информации о клиенте
                .then()
                .log().all(); // Логируем ответ
    }

    // Метод для создания нового клиента
    @Step("creating client")
    public ValidatableResponse createClient(Client client) {
        return given()
                .spec(getSpec())
                .body(client) // В теле запроса передаем данные нового клиента
                .when()
                .post(CREATE_CLIENT_PATH) // Отправляем POST запрос для регистрации клиента
                .then(); // Возвращаем результат выполнения запроса
    }

    // Метод для входа клиента (логин)
    @Step("login client")
    public ValidatableResponse loginClient(Client client, String accessToken) {
        return given()
                .spec(getSpec())
                .auth().oauth2(accessToken)
                .body(client) // В теле запроса передаем данные клиента для логина
                .log().all() // Логируем запрос
                .post(LOGIN_CLIENT_PATH) // Отправляем POST запрос для логина клиента
                .then()
                .log().all(); // Логируем ответ
    }

    // Метод для выхода клиента (лог-аут)
    @Step("logout client")
    public ValidatableResponse logoutClient(String refreshToken) {
        return given()
                .spec(getSpec())
                .body(refreshToken)
                .log().all() // Логируем запрос
                .post(LOGOUT_CLIENT_PATH) // Отправляем POST запрос для выхода клиента
                .then()
                .log().all(); // Логируем ответ
    }

    // Метод для удаления клиента
    @Step("delete client")
    public ValidatableResponse deleteClient(String accessToken) {
        return given()
                .spec(getSpec())
                .auth().oauth2(accessToken)
                .log().all() // Логируем запрос
                .delete(DELETE_CLIENT_PATH) // Отправляем DELETE запрос для удаления клиента
                .then()
                .log().all(); // Логируем ответ
    }

    // Метод для обновления информации о клиенте с авторизацией
    @Step("update client by authorization")
    public ValidatableResponse updateClientByAuthorization(Client client, String accessToken) {
        return given()
                .spec(getSpec())
                .header("Authorization", accessToken)
                .body(client) // В теле запроса передаем обновленные данные клиента
                .log().all() // Логируем запрос
                .patch(CLIENT_PATH) // Отправляем PATCH запрос для обновления информации о клиенте
                .then()
                .log().all(); // Логируем ответ
    }

    // Метод для обновления информации о клиенте без авторизации
    @Step("update client without authorization")
    public ValidatableResponse updateClientWithoutAuthorization(Client client) {
        return given()
                .spec(getSpec())
                .body(client) // В теле запроса передаем обновленные данные клиента
                .log().all() // Логируем запрос
                .patch(CLIENT_PATH) // Отправляем PATCH запрос для обновления информации о клиенте
                .then()
                .log().all(); // Логируем ответ
    }
}