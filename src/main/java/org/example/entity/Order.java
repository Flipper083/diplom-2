package org.example.entity;

import java.util.List;
import java.util.ArrayList;
import lombok.Data;
import lombok.AllArgsConstructor;

@Data
@AllArgsConstructor
public class Order {
    private List<String> ingredients; // Список ингредиентов для заказа (список строк, представляющих идентификаторы ингредиентов)

    // Конструктор без параметров, инициализирует список ингредиентов
    public Order() {
        ingredients = new ArrayList<>(); // Инициализация пустого списка ингредиентов
    }
}
