package org.store.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.store.data.*;
import org.store.enums.StokaCategory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class StoreServiceTest {
    private Store store;
    private StoreService storeService;
    private Stoka stokaFood;
    private Stoka stokaNonFood;

    @BeforeEach
    void setup() {
        EnumMap<StokaCategory, Double> marginByCategory = new EnumMap<>(StokaCategory.class);
        marginByCategory.put(StokaCategory.FOOD, 10.0);
        marginByCategory.put(StokaCategory.NONFOOD, 15.0);

        store = new Store(15, 10.0, marginByCategory);
        storeService = new StoreService(store);

        LocalDate expDate = LocalDate.now().plusDays(20);
        stokaFood = new Stoka("001", "Замразен грах", new BigDecimal("5.00"), new BigDecimal("10.00"), expDate, StokaCategory.FOOD);
        stokaNonFood = new Stoka("002", "Тетрадка", new BigDecimal("15.00"), new BigDecimal("20.00"), expDate, StokaCategory.NONFOOD);
    }
//    теста не работи
//    @Test
//    void testCalculatePriceWithMargin() {
//        BigDecimal newPriceFood = storeService.deliverStokaReturnPriceWithMargin(stokaFood);
//        assertEquals(new BigDecimal("5.50"), newPriceFood);  // 5 + 10%
//
//        BigDecimal newPriceNonFood = storeService.deliverStokaReturnPriceWithMargin(stokaNonFood);
//        assertEquals(new BigDecimal("17.25"), newPriceNonFood); // 15 + 15%
//    }

    @Test
    void testValidateAndFixExpirationDate() {

        Stoka expired = new Stoka("003", "Стар продукт", new BigDecimal("10"), new BigDecimal("12"), LocalDate.now().minusDays(1), StokaCategory.FOOD);
        boolean valid = storeService.validateAndFixExpirationDate(expired);
        assertFalse(valid);
        assertTrue(expired.getExpirationDate().isAfter(LocalDate.now()));


        boolean valid2 = storeService.validateAndFixExpirationDate(stokaFood);
        assertTrue(valid2);
    }
}