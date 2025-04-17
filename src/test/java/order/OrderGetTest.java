package order;

import io.qameta.allure.Epic;
import io.qameta.allure.Description; // Импортируем аннотацию Description
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.apache.commons.lang3.StringUtils;
import org.example.api.OrderClient;
import org.example.api.UserClient;
import org.example.entity.Client;
import org.example.entity.Order;
import org.example.utils.ClientGenerator;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

@Epic("Get order and ingredients")
public class OrderGetTest {
    // Константа для сообщения об ошибке неавторизованного доступа
    private static final String MESSAGE_UNAUTHORIZED = "You should be authorised";

    // Переменные для хранения ответов, клиента, заказа и клиентов API
    private ValidatableResponse response;
    private Client client;
    private Order order;
    private UserClient userClient;
    private OrderClient orderClient;

    // Метод для заполнения списка ингредиентов для заказа
    private void fillListIngredients() {
        // Получаем все ингредиенты
        response = orderClient.getAllIngredients();
        // Извлекаем идентификаторы ингредиентов
        List<String> list = response.extract().path("data._id");
        // Получаем список ингредиентов из заказа
        List<String> ingredients = order.getIngredients();
        // Добавляем ингредиенты в заказ
        ingredients.add(list.get(0)); // Добавляем первый ингредиент
        ingredients.add(list.get(5)); // Добавляем шестой ингредиент
        ingredients.add(list.get(0)); // Добавляем первый ингредиент снова
    }

    @Before
    public void setUp() {
        // Генерируем случайного клиента
        client = ClientGenerator.getRandomClient();
        // Создаем новый заказ
        order = new Order();
        // Инициализируем клиентов API
        userClient = new UserClient();
        orderClient = new OrderClient();
        // Заполняем ингредиенты для заказа
        fillListIngredients();
    }

    @Test
    @DisplayName("Get all ingredients")
    @Description("This test verifies that all ingredients can be retrieved successfully.")
    public void getAllIngredientsTest() {
        // Получаем все ингредиенты
        response = orderClient.getAllIngredients();
        // Извлекаем статус-код и проверяем успешность запроса
        int statusCode = response.extract().statusCode();
        boolean isGet = response.extract().path("success");

        // Проверяем, что статус-код равен 200 (SC_OK)
        assertThat("Code not equal", statusCode, equalTo(SC_OK));
        // Проверяем, что ингредиенты получены корректно
        assertThat("Ingredients is get incorrect", isGet, equalTo(true));
    }

    @Test
    @DisplayName("Get order by authorization client")
    @Description("This test verifies that a client can retrieve their order with valid authorization.")
    public void getOrderByAuthorizationClientTest() {
        // Создаем клиента и получаем токен доступа
        response = userClient.createClient(client);
        String accessToken = response.extract().path("accessToken");
        // Авторизуем клиента
        response = userClient.loginClient(client, accessToken);
        // Создаем заказ с авторизацией
        response = orderClient.createOrderByAuthorization(order, accessToken);
        // Получаем заказы с авторизацией
        response = orderClient.getOrdersByAuthorization(accessToken);
        // Извлекаем статус-код и проверяем успешность запроса
        int statusCode = response.extract().statusCode();
        boolean isGet = response.extract().path("success");
        // Удаляем клиента
        response = userClient.deleteClient(StringUtils.substringAfter(accessToken, " "));

        // Проверяем, что статус-код равен 200 (SC_OK)
        assertThat("Code not equal", statusCode, equalTo(SC_OK));
        // Проверяем, что заказ получен корректно
        assertThat("Order is get incorrect", isGet, equalTo(true));
    }

    @Test
    @DisplayName("Get order without authorization client")
    @Description("This test verifies that a client cannot retrieve their order without authorization.")
    public void getOrderWithoutAuthorizationUserTest() {
        // Создаем заказ без авторизации
        response = orderClient.createOrderWithoutAuthorization(order);
        // Пытаемся получить заказы без авторизации
        response = orderClient.getOrdersWithoutAuthorization();
        // Извлекаем статус-код и сообщение об ошибке
        int statusCode = response.extract().statusCode();
        String message = response.extract().path("message");
        boolean isGet = response.extract().path("success");

        // Проверяем, что статус-код равен 401 (SC_UNAUTHORIZED)
        assertThat("Code not equal", statusCode, equalTo(SC_UNAUTHORIZED));
        // Проверяем, что сообщение об ошибке соответствует ожидаемому
        assertThat("Message not equal", message, equalTo(MESSAGE_UNAUTHORIZED));
        // Проверяем, что заказ не был получен
        assertThat("Order is get correct", isGet, equalTo(false));
    }
}
