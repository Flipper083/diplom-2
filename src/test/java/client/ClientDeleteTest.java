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
import static org.apache.http.HttpStatus.SC_ACCEPTED;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

@Epic("Delete client")
public class ClientDeleteTest {
    private static final String MESSAGE_ACCEPTED = "User successfully removed"; // Сообщение об успешном удалении клиента
    private UserClient userClient; // Переменная для работы с API пользователей
    private Client client; // Переменная для хранения клиента

    @Before
    public void setUp() {
        client = ClientGenerator.getRandomClient(); // Генерируем случайного клиента
        userClient = new UserClient();
    }

    @Test
    @DisplayName("Client delete by valid credentials") // Название теста для отображения в отчетах
    public void clientDeleteByValidCredentials() {
        ValidatableResponse response = userClient.createClient(client); // Создаем клиента через API и получаем ответ
        String accessToken = response.extract().path("accessToken");
        response = userClient.deleteClient(StringUtils.substringAfter(accessToken, " ")); // Удаляем клиента по accessToken
        int statusCode = response.extract().statusCode(); // Извлекаем статус код ответа
        String message = response.extract().path("message"); // Извлекаем сообщение об успешном удалении
        boolean isDelete = response.extract().path("success"); // Проверяем успешность удаления клиента

        // Проверяем accessToken на null, статус код, сообщение и успешность удаления клиента
        assertThat("Token is null", accessToken, notNullValue()); // Проверка на то, что accessToken не null
        assertThat("Code not equal", statusCode, equalTo(SC_ACCEPTED)); // Проверка на соответствие статус кода
        assertThat("Message not equal", message, equalTo(MESSAGE_ACCEPTED)); // Проверка на соответствие сообщения
        assertThat("Client is delete incorrect", isDelete, equalTo(true)); // Проверка на успешное удаление клиента
    }
}
