package org.store.data;

import org.store.enums.StokaCategory;

import java.math.BigDecimal;
import java.time.LocalDate;

// Хранителна стока
public class FoodStoka extends Stoka {

    public FoodStoka(String id, String name, BigDecimal deliveryPrice, BigDecimal price,
                     LocalDate expirationDate) {
        // Задаваме категорията директно тук
        super(id, name, deliveryPrice, price, expirationDate, StokaCategory.FOOD);
    }

    // Можеш да добавиш допълнителни методи или да override-неш някой от базовия клас, ако е нужно
    // Например: проверка на срока на годност (вече имаш такъв метод в Stoka)
}
