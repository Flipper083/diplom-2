package client;

import io.qameta.allure.Epic;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.apache.commons.lang3.StringUtils;
import org.example.entity.Client;
import org.example.utils.ClientGenerator;
import org.example.api.UserClient;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

@Epic("Login and logout client")
public class ClientLoginLogoutTest {
    private static final String MESSAGE_LOGOUT = "Successful logout"; // Сообщение об успешном выходе
    private static final String MESSAGE_UNAUTHORIZED = "email or password are incorrect"; // Сообщение об ошибке авторизации
    private ValidatableResponse response; // Переменная для хранения ответа от API
    private UserClient userClient; // Переменная для работы с API пользователей
    private Client client; // Переменная для хранения клиента
    private String accessToken; // Переменная для хранения accessToken

    @Before
    public void setUp() {
        client = ClientGenerator.getRandomClient(); // Генерируем случайного клиента
        userClient = new UserClient();
    }

    @After
    public void clearState() {
        userClient.deleteClient(StringUtils.substringAfter(accessToken, " ")); // Удаляем клиента по accessToken
    }

    @Test
    @DisplayName("Client login by valid credentials")
    public void clientLoginByValidCredentials() {
        response = userClient.createClient(client); // Создаем клиента через API и получаем ответ
        accessToken = response.extract().path("accessToken"); // Извлекаем accessToken из ответа
        response = userClient.loginClient(client, accessToken); // Пытаемся войти в систему с учетными данными клиента
        int statusCode = response.extract().statusCode(); // Извлекаем статус код ответа
        boolean isLogin = response.extract().path("success"); // Проверяем успешность входа

        // Проверяем, что accessToken не равен null и что вход выполнен успешно
        assertThat("Token is null", accessToken, notNullValue()); // Проверка на наличие accessToken
        assertThat("Code not equal", statusCode, equalTo(SC_OK)); // Проверка на соответствие статус кода
        assertThat("Client is login incorrect", isLogin, equalTo(true)); // Проверка на успешный вход
    }

    @Test
    @DisplayName("Client logout by valid credentials")
    public void clientLogoutByValidCredentials() {
        response = userClient.createClient(client); // Создаем клиента через API
        accessToken = response.extract().path("accessToken"); // Извлекаем accessToken
        response = userClient.loginClient(client, accessToken); // Пытаемся войти в систему
        String refreshToken = response.extract().path("refreshToken"); // Извлекаем refreshToken
        refreshToken = "{\"token\":\"" + refreshToken + "\"}"; // Формируем JSON с refreshToken
        response = userClient.logoutClient(refreshToken); // Пытаемся выйти из системы
        int statusCode = response.extract().statusCode();         // Извлекаем статус код ответа
        String message = response.extract().path("message"); // Извлекаем сообщение из ответа
        boolean isLogout = response.extract().path("success"); // Проверяем успешность выхода

        // Проверяем, что refreshToken не равен null и что выход выполнен успешно
        assertThat("Token is null", refreshToken, notNullValue()); // Проверка на наличие refreshToken
        assertThat("Code not equal", statusCode, equalTo(SC_OK)); // Проверка на соответствие статус кода
        assertThat("Message not equal", message, equalTo(MESSAGE_LOGOUT)); // Проверка на соответствие сообщения
        assertThat("Client is logout incorrect", isLogout, equalTo(true)); // Проверка на успешный выход
    }

    @Test
    @DisplayName("Client login is empty email")
    public void clientLoginByEmptyEmail() {
        response = userClient.createClient(client); // Создаем клиента через API
        accessToken = response.extract().path("accessToken"); // Извлекаем accessToken
        client.setEmail(null); // Устанавливаем email клиента в null
        response = userClient.loginClient(client, accessToken); // Пытаемся войти в систему с пустым email
        int statusCode = response.extract().statusCode(); // Извлекаем статус код ответа
        String message = response.extract().path("message"); // Извлекаем сообщение из ответа
        boolean isLogin = response.extract().path("success"); // Проверяем успешность входа

        // Проверяем, что accessToken не равен null и что вход не выполнен
        assertThat("Token is null", accessToken, notNullValue()); // Проверка на наличие accessToken
        assertThat("Code not equal", statusCode, equalTo(SC_UNAUTHORIZED)); // Проверка на соответствие статус кода
        assertThat("Message not equal", message, equalTo(MESSAGE_UNAUTHORIZED)); // Проверка на соответствие сообщения
        assertThat("Client is login correct", isLogin, equalTo(false)); // Проверка на неуспешный вход
    }

    @Test
    @DisplayName("Client login is empty password")
    public void clientLoginByEmptyPasswordTest() {
        response = userClient.createClient(client); // Создаем клиента через API
        accessToken = response.extract().path("accessToken"); // Извлекаем accessToken
        client.setPassword(null); // Устанавливаем пароль клиента в null
        response = userClient.loginClient(client, accessToken); // Пытаемся войти в систему с пустым паролем
        int statusCode = response.extract().statusCode(); // Извлекаем статус код ответа
        String message = response.extract().path("message"); // Извлекаем сообщение из ответа
        boolean isLogin = response.extract().path("success"); // Проверяем успешность входа

        // Проверяем, что accessToken не равен null и что вход не выполнен
        assertThat("Token is null", accessToken, notNullValue()); // Проверка на наличие accessToken
        assertThat("Code not equal", statusCode, equalTo(SC_UNAUTHORIZED)); // Проверка на соответствие статус кода
        assertThat("Message not equal", message, equalTo(MESSAGE_UNAUTHORIZED)); // Проверка на соответствие сообщения
        assertThat("Client is login correct", isLogin, equalTo(false)); // Проверка на неуспешный вход
    }
}