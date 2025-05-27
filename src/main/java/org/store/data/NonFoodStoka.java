package org.store.data;

import org.store.enums.StokaCategory;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.store.enums.StokaCategory.NONFOOD;

// Нехранителна стока
public class NonFoodStoka extends Stoka {



    public NonFoodStoka(String id, String name, BigDecimal deliveryPrice, BigDecimal price) {
        // Нехранителната стока няма срок на годност, подаваме null
        super(id, name, deliveryPrice, price, null, NONFOOD);
    }

    // Можеш да override-неш методите за проверка на годност, ако не са приложими

    @Override
    public boolean isExpired() {
        // Нехранителната стока не изтича
        return false;
    }

    @Override
    public boolean isValid() {
        return true;
    }


}
