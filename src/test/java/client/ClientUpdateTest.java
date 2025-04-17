package client;

import io.qameta.allure.Epic;
import io.qameta.allure.Description; // Импортируем аннотацию Description
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
import static org.hamcrest.MatcherAssert.assertThat;

@Epic("Update client")
public class ClientUpdateTest {

    private static final String MESSAGE_UNAUTHORIZED = "You should be authorised"; // Сообщение для неавторизованного пользователя
    private ValidatableResponse response; // Переменная для хранения ответов API
    private UserClient userClient; // Экземпляр клиента для работы с API
    private Client client; // Экземпляр клиента
    private String accessToken; // Токен доступа для авторизованных запросов

    @Before
    public void setUp() {
        // Генерация случайного клиента перед каждым тестом
        client = ClientGenerator.getRandomClient();
        userClient = new UserClient(); // Инициализация API клиента

        // Создаем нового клиента и получаем токен доступа
        response = userClient.createClient(client);
        accessToken = response.extract().path("accessToken");
    }

    @After
    public void clearState() {
        // Удаление клиента после теста, используя токен доступа
        if (accessToken != null) {
            userClient.deleteClient(StringUtils.substringAfter(accessToken, " "));
        }
    }

    @Test
    @DisplayName("Update client by authorization") // Название теста
    @Description("This test verifies that a client can update their information with valid authorization.")
    public void updateClientByAuthorizationTest() {
        // Логиним клиента с использованием токена
        response = userClient.loginClient(client, accessToken);

        // Обновляем информацию клиента по авторизованному запросу
        Client updatedClient = ClientGenerator.getRandomClient();
        response = userClient.updateClientByAuthorization(updatedClient, accessToken);

        // Извлекаем статус-код и данные о успешности обновления
        int statusCode = response.extract().statusCode();
        boolean isUpdate = response.extract().path("success");

        // Проверяем успешность обновления
        assertThat("Code not equal", statusCode, equalTo(SC_OK)); // Статус ответа должен быть 200 OK
        assertThat("Client is update incorrect", isUpdate, equalTo(true)); // Ответ должен подтверждать успешное обновление

        // Проверяем, что обновленные данные корректны
        assertThat("Email not updated", response.extract().path("client.email"), equalTo(updatedClient.getEmail()));
        assertThat("Name not updated", response.extract().path("client.name"), equalTo(updatedClient.getName()));
    }

    @Test
    @DisplayName("Update client email by authorization") // Название теста
    @Description("This test verifies that a client can update their email with valid authorization.")
    public void updateClientEmailByAuthorizationTest() {
        // Логиним клиента с использованием токена
        response = userClient.loginClient(client, accessToken);

        // Обновляем email клиента
        String newEmail = "newemail@example.com";
        client.setEmail(newEmail);
        response = userClient.updateClientByAuthorization(client, accessToken);

        // Извлекаем статус-код и данные о успешности обновления
        int statusCode = response.extract().statusCode();
        boolean isUpdate = response.extract().path("success");

        // Проверяем успешность обновления
        assertThat("Code not equal", statusCode, equalTo(SC_OK));
        assertThat("Client is update incorrect", isUpdate, equalTo(true));
        assertThat("Email not updated", response.extract().path("client.email"), equalTo(newEmail)); // Проверка обновленного email
    }

    @Test
    @DisplayName("Update client name by authorization") // Название теста
    @Description("This test verifies that a client can update their name with valid authorization.")
    public void updateClientNameByAuthorizationTest() {
        // Логиним клиента с использованием токена
        response = userClient.loginClient(client, accessToken);

        // Обновляем имя клиента
        String newName = "New Name";
        client.setName(newName);
        response = userClient.updateClientByAuthorization(client, accessToken);

        // Извлекаем статус-код и данные о успешности обновления
        int statusCode = response.extract().statusCode();
        boolean isUpdate = response.extract().path("success");

        // Проверяем успешность обновления
        assertThat("Code not equal", statusCode, equalTo(SC_OK));
        assertThat("Client is update incorrect", isUpdate, equalTo(true));
        assertThat("Name not updated", response.extract().path("client.name"), equalTo(newName)); // Проверка обновленного имени
    }

    @Test
    @DisplayName("Update client without authorization") // Название теста
    @Description("This test verifies that a client cannot update their information without authorization.")
    public void updateClientWithoutAuthorizationTest() {
        // Пытаемся обновить информацию клиента без авторизации
        response = userClient.updateClientWithoutAuthorization(ClientGenerator.getRandomClient());

        // Извлекаем статус-код и сообщение об ошибке
        int statusCode = response.extract().statusCode();
        String message = response.extract().path("message");
        boolean isUpdate = response.extract().path("success");

        // Проверяем, что обновление без авторизации не произошло
        assertThat("Code not equal", statusCode, equalTo(SC_UNAUTHORIZED)); // Статус ответа должен быть 401 Unauthorized
        assertThat("Message not equal", message, equalTo(MESSAGE_UNAUTHORIZED)); // Сообщение должно быть о необходимости авторизации
        assertThat("User is update correct", isUpdate, equalTo(false)); // Обновление не должно быть успешным
    }
}
