package client;

import io.qameta.allure.Epic;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.apache.commons.lang3.StringUtils;
import org.example.entity.Client;
import org.example.utils.ClientGenerator;
import org.example.api.UserClient;
import org.junit.Before;
import org.junit.Test;
import static org.apache.http.HttpStatus.SC_FORBIDDEN;
import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

@Epic("Create client")
public class ClientCreateTest {
    private static final String MESSAGE_FORBIDDEN = "User already exists"; // Сообщение о том, что пользователь уже существует
    private static final String MESSAGE_FORBIDDEN_EMPTY_FIELD = "Email, password and name are required fields"; // Сообщение об ошибке при пустом поле
    private ValidatableResponse response;
    private UserClient userClient;
    private Client client;

    @Before // Метод, который выполняется перед каждым тестом
    public void setUp() {
        client = ClientGenerator.getRandomClient(); // Генерируем случайного клиента
        userClient = new UserClient(); // Инициализируем объект
    }

    @Test
    @DisplayName("Client create by valid credentials")
    public void clientCreateByValidCredentialsTest() {
        response = userClient.createClient(client); // Создаем клиента через API
        int statusCode = response.extract().statusCode(); // Извлекаем статус код ответа
        boolean isCreate = response.extract().path("success"); // Проверяем успешность создания клиента
        String accessToken = response.extract().path("accessToken"); // Извлекаем accessToken из ответа
        response = userClient.deleteClient(StringUtils.substringAfter(accessToken, " ")); // Удаляем клиента по accessToken

        // Проверяем статус код и успешность создания клиента
        assertThat("Code not equal", statusCode, equalTo(SC_OK)); // Проверка на соответствие статус кода
        assertThat("Client is create incorrect", isCreate, equalTo(true)); // Проверка на успешное создание клиента
    }

    @Test
    @DisplayName("Client create is empty email")
    public void clientCreateIsEmptyEmailTest() {
        client.setEmail(null); // Устанавливаем email клиента в null
        response = userClient.createClient(client); // Пытаемся создать клиента
        int statusCode = response.extract().statusCode(); // Извлекаем статус код ответа
        String message = response.extract().path("message"); // Извлекаем сообщение об ошибке
        boolean isCreate = response.extract().path("success"); // Проверяем успешность создания клиента

        // Проверяем статус код и сообщение об ошибке
        assertThat("Code not equal", statusCode, equalTo(SC_FORBIDDEN)); // Проверка на соответствие статус кода
        assertThat("Message not equal", message, equalTo(MESSAGE_FORBIDDEN_EMPTY_FIELD)); // Проверка на соответствие сообщения
        assertThat("Client is create correct", isCreate, equalTo(false)); // Проверка на то, что клиент не был создан
    }

    @Test
    @DisplayName("Client create is empty password")
    public void clientCreateIsEmptyPasswordTest() {
        client.setPassword(null); // Устанавливаем пароль клиента в null
        response = userClient.createClient(client); // Пытаемся создать клиента
        int statusCode = response.extract().statusCode(); // Извлекаем статус код ответа
        String message = response.extract().path("message"); // Извлекаем сообщение об ошибке
        boolean isCreate = response.extract().path("success"); // Проверяем успешность создания клиента

        // Проверяем статус код и сообщение об ошибке
        assertThat("Code not equal", statusCode, equalTo(SC_FORBIDDEN)); // Проверка на соответствие статус кода
        assertThat("Message not equal", message, equalTo(MESSAGE_FORBIDDEN_EMPTY_FIELD)); // Проверка на соответствие сообщения
        assertThat("Client is create correct", isCreate, equalTo(false)); // Проверка на то, что клиент не был создан
    }

    @Test
    @DisplayName("Client create is empty name")
    public void clientCreateIsEmptyNameTest() {
        client.setName(null); // Устанавливаем имя клиента в null
        response = userClient.createClient(client); // Пытаемся создать клиента
        int statusCode = response.extract().statusCode(); // Извлекаем статус код ответа
        String message = response.extract().path("message"); // Извлекаем сообщение об ошибке
        boolean isCreate = response.extract().path("success"); // Проверяем успешность создания клиента

        // Проверяем статус код и сообщение об ошибке
        assertThat("Code not equal", statusCode, equalTo(SC_FORBIDDEN)); // Проверка на соответствие статус кода
        assertThat("Message not equal", message, equalTo(MESSAGE_FORBIDDEN_EMPTY_FIELD)); // Проверка на соответствие сообщения
        assertThat("Client is create correct", isCreate, equalTo(false)); // Проверка на то, что клиент не был создан
    }

    @Test
    @DisplayName("Repeated request by create client")
    public void repeatedRequestByCreateClientTest() {
        userClient.createClient(client); // Создаем клиента
        response = userClient.createClient(client); // Пытаемся создать клиента повторно
        int statusCode = response.extract().statusCode(); // Извлекаем статус код ответа
        String message = response.extract().path("message"); // Извлекаем сообщение об ошибке
        boolean isCreate = response.extract().path("success"); // Проверяем успешность создания клиента

        // Проверяем статус код и сообщение об ошибке
        assertThat("Code not equal", statusCode, equalTo(SC_FORBIDDEN)); // Проверка на соответствие статус кода
        assertThat("Message not equal", message, equalTo(MESSAGE_FORBIDDEN)); // Проверка на соответствие сообщения
        assertThat("Client is create correct", isCreate, equalTo(false)); // Проверка на то, что клиент не был создан
    }
}
