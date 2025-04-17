package org.example.utils;

import io.qameta.allure.Allure;
import org.apache.commons.lang3.RandomStringUtils;
import org.example.entity.Client;

public class ClientGenerator {
    public static Client getRandomClient() {
        // Генерируем случайное имя, пароль и email для клиента
        final String name = RandomStringUtils.randomAlphabetic(8); // Генерируем случайное имя из 8 букв
        final String password = RandomStringUtils.randomAlphabetic(8); // Генерируем случайный пароль из 8 символов
        final String email = name.toLowerCase() + "@yandex.ru"; // Создаем email из имени и домена yandex.ru

        // Добавляем сгенерированные данные в отчет Allure
        Allure.addAttachment("Name : ", name); // Добавляем имя в отчет Allure
        Allure.addAttachment("Password : ", password); // Добавляем пароль в отчет Allure
        Allure.addAttachment("Email : ", email); // Добавляем email в отчет Allure

        // Возвращаем новый объект Client с сгенерированными данными
        return new Client(email, password, name);
    }
}
