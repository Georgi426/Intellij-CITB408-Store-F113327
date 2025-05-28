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

    // Може да добавя допълнителни методи или да override-на някой от
    // базовия клас, ако е нужно

}
