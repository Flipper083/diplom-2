package org.example.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// Класс, представляющий сущность клиента
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL) // Указывает, что поля с null значениями не будут включены в JSON
public class Client {
    private String email; // Электронная почта клиента
    private String password; // Пароль клиента
    private String name; // Имя клиента
}
