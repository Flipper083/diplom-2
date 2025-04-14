package order;

import io.qameta.allure.Epic;
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

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;


@Epic("Create order")
public class OrderCreateTest {

    // Константа для сообщения об ошибке при неправильном запросе
    private static final String MESSAGE_BAD_REQUEST = "Ingredient ids must be provided";

    // Объявление переменных для ответа от сервера, клиента и заказа
    private ValidatableResponse response;
    private Client client;
    private Order order;
    private UserClient userClient;
    private OrderClient orderClient;

    // Метод для добавления ингредиентов в заказ
    private void fillListIngredients() {
        // Получаем список ингредиентов через API
        response = orderClient.getAllIngredients();
        List<String> list = response.extract().path("data._id"); // Извлекаем список ингредиентов
        List<String> ingredients = order.getIngredients(); // Получаем ингредиенты для текущего заказа
        // Добавляем несколько ингредиентов в заказ
        ingredients.add(list.get(0));
        ingredients.add(list.get(5));
        ingredients.add(list.get(0));
    }

    // Метод для настройки перед каждым тестом
    @Before
    public void setUp() {
        // Генерация случайного клиента и создание нового заказа
        client = ClientGenerator.getRandomClient();
        order = new Order();
        // Инициализация клиентов для работы с API
        userClient = new UserClient();
        orderClient = new OrderClient();
    }

    // Тест: Создание заказа с авторизацией
    @Test
    @DisplayName("Create order by authorization")
    public void orderCreateByAuthorizationTest() {
        fillListIngredients(); // Добавляем ингредиенты в заказ
        // Создаем клиента через API и получаем токен
        response = userClient.createClient(client);
        String accessToken = response.extract().path("accessToken");
        // Логиним клиента с полученным токеном
        response = userClient.loginClient(client, accessToken);
        // Создаем заказ с авторизацией
        response = orderClient.createOrderByAuthorization(order, accessToken);
        // Извлекаем данные из ответа
        int statusCode = response.extract().statusCode();
        boolean isCreate = response.extract().path("success");
        int orderNumber = response.extract().path("order.number");
        String orderId = response.extract().path("order._id");
        // Удаляем клиента после теста
        response = userClient.deleteClient(StringUtils.substringAfter(accessToken, " "));

        // Проверяем ответы от сервера
        assertThat("Code not equal", statusCode, equalTo(SC_OK));
        assertThat("Order is create incorrect", isCreate, equalTo(true));
        assertThat("Order number is null", orderNumber, notNullValue());
        assertThat("Order id is null", orderId, notNullValue());
    }

    // Тест: Создание заказа без авторизации
    @Test
    @DisplayName("Create order without authorization")
    public void orderCreateWithoutAuthorizationTest() {
        fillListIngredients(); // Добавляем ингредиенты в заказ
        // Создаем заказ без авторизации
        response = orderClient.createOrderWithoutAuthorization(order);
        // Извлекаем данные из ответа
        int statusCode = response.extract().statusCode();
        boolean isCreate = response.extract().path("success");
        int orderNumber = response.extract().path("order.number");

        // Проверяем ответы от сервера
        assertThat("Code not equal", statusCode, equalTo(SC_OK));
        assertThat("Order is create incorrect", isCreate, equalTo(true));
        assertThat("Order number is null", orderNumber, notNullValue());
    }

    // Тест: Создание заказа без авторизации и без ингредиентов
    @Test
    @DisplayName("Create order without authorization and ingredients")
    public void orderCreateWithoutAuthorizationAndIngredientsTest() {
        // Создаем заказ без авторизации и без ингредиентов
        response = orderClient.createOrderWithoutAuthorization(order);
        // Извлекаем данные из ответа
        int statusCode = response.extract().statusCode();
        String message = response.extract().path("message");
        boolean isCreate = response.extract().path("success");

        // Проверяем, что ошибка возвращена правильно
        assertThat("Code not equal", statusCode, equalTo(SC_BAD_REQUEST));
        assertThat("Message not equal", message, equalTo(MESSAGE_BAD_REQUEST));
        assertThat("Order is create correct", isCreate, equalTo(false));
    }

    // Тест: Создание заказа без авторизации с некорректным хешом ингредиента
    @Test
    @DisplayName("Create order without authorization and change hash ingredient")
    public void orderCreateWithoutAuthorizationAndChangeHashIngredientTest() {
        // Получаем список ингредиентов через API
        response = orderClient.getAllIngredients();
        List<String> list = response.extract().path("data._id");
        List<String> ingredients = order.getIngredients();
        // Добавляем ингредиенты с некорректным хешом (заменяем букву в ID)
        ingredients.add(list.get(0));
        ingredients.add(list.get(5).replaceAll("a", "l"));
        ingredients.add(list.get(0));
        // Создаем заказ без авторизации
        response = orderClient.createOrderWithoutAuthorization(order);
        // Извлекаем статус код из ответа
        int statusCode = response.extract().statusCode();

        // Проверяем, что вернулся статус ошибки 500
        assertThat("Code not equal", statusCode, equalTo(SC_INTERNAL_SERVER_ERROR));
    }
}
