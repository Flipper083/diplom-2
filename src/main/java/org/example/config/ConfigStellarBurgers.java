package org.example.config;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

public class ConfigStellarBurgers {
    // Базовый URL для API
    private static final String BASE_URL = "https://stellarburgers.nomoreparties.site/";

    // Метод для получения спецификации запроса, включая тип контента и базовый URI
    protected RequestSpecification getSpec(){
        return new RequestSpecBuilder() // Строим спецификацию для запроса
                .setContentType(ContentType.JSON) // Устанавливаем тип контента как JSON
                .setBaseUri(BASE_URL) // Устанавливаем базовый URI для API
                .build(); // Строим и возвращаем спецификацию
    }
}