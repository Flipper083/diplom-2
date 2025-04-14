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
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

@Epic("Get client")
public class ClientGetTest {
    private UserClient userClient;
    private Client client;
    private String accessToken;

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
    @DisplayName("Get client by valid credentials")
    public void clientGetByValidCredentialsTest() {
        ValidatableResponse response = userClient.createClient(client); // Создаем клиента через API и получаем ответ
        accessToken = response.extract().path("accessToken"); // Извлекаем accessToken из ответа
        response = userClient.getClient(accessToken); // Получаем информацию о клиенте по accessToken
        int statusCode = response.extract().statusCode(); // Извлекаем статус код ответа
        boolean isGet = response.extract().path("success"); // Проверяем успешность получения информации о клиенте
        String email = response.extract().path("client.email"); // Извлекаем email клиента из ответа
        String name = response.extract().path("client.name"); // Извлекаем имя клиента из ответа

        // Проверяем статус код, успешность получения информации и соответствие email и имени клиента
        assertThat("Code not equal", statusCode, equalTo(SC_OK)); // Проверка на соответствие статус кода
        assertThat("Client is get incorrect", isGet, equalTo(true)); // Проверка на успешное получение информации о клиенте
        assertThat("Email not equal", email, equalTo(client.getEmail())); // Проверка на соответствие email клиента
        assertThat("Name not equal", name, equalTo(client.getName())); // Проверка на соответствие имени клиента
    }
}