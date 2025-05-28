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

//    @Test
//    void testCalculatePriceWithMargin() {
//        BigDecimal newPriceFood = storeService.deliverStokaReturnPriceWithMargin(stokaFood);
//        assertEquals(new BigDecimal("5.50"), newPriceFood);  // 5 + 10%
//
//        BigDecimal newPriceNonFood = storeService.deliverStokaReturnPriceWithMargin(stokaNonFood);
//        assertEquals(new BigDecimal("17.25"), newPriceNonFood); // 15 + 15%
//    }

    //Проверява дали методът открива стока с изтекъл срок на годност и го коригира с нова дата. За валидна стока с бъдеща дата се очаква да я приеме без промяна.
    @Test
    void testValidateAndFixExpirationDate() {

        Stoka expired = new Stoka("003", "Стар продукт", new BigDecimal("10"), new BigDecimal("12"), LocalDate.now().minusDays(1), StokaCategory.FOOD);
        boolean valid = storeService.validateAndFixExpirationDate(expired);
        assertFalse(valid);
        assertTrue(expired.getExpirationDate().isAfter(LocalDate.now()));


        boolean valid2 = storeService.validateAndFixExpirationDate(stokaFood);
        assertTrue(valid2);
    }


    //Създава карта с продадени стоки и добавя информацията в магазина чрез addSoldStoka(). След това проверява дали правилното количество е записано.
    @Test
    void testAddSoldStoka() {
        Map<Stoka, Double> soldMap = new HashMap<>();
        soldMap.put(stokaFood, 3.0);
        storeService.addSoldStoka(soldMap);

        assertEquals(3.0, store.getSoldStoka().get(stokaFood));
    }


    //Симулира добавяне на продукти в инвентара и после премахва част от тях чрез removeFromInventory(). Уверява се, че остатъчното количество е коректно.
    @Test
    void testRemoveFromInventory() {
        // Add items first
        store.addToInventory(stokaFood);
        store.addToInventory(stokaFood);
        store.addToInventory(stokaFood);
        Map<Stoka, Double> sold = new HashMap<>();
        sold.put(stokaFood, 2.0);

        storeService.removeFromInventory(sold);

        assertEquals(1.0, store.getInventory().get(stokaFood));
    }

//
//      Тестът проверява правилното изчисляване на печалбата на магазина.
//      Симулира се работна среда с:
//      1. Добавен касиер с месечна заплата
//      2. Доставена стока с цена на доставка
//      3. Продадена стока с определено количество
//
//      Печалбата се изчислява като разлика между приходите от продажби и разходите
//      (заплати на касиери + разходи за доставка).
//
        @Test
        void testCalculateStoreProfit() {
        // Add cashier
        Cashier cashier = new Cashier("Ben", new BigDecimal("1000"));
        store.addCashier(cashier);

        // Add delivered stoka with delivery price
        stokaFood.setDeliveryPrice(new BigDecimal("1.00"));
        store.addToDeliveredStoka(stokaFood);

        // Add sold stoka
        Map<Stoka, Double> soldMap = new HashMap<>();
        soldMap.put(stokaFood, 10.0);
        store.setSoldStoka(soldMap);

        BigDecimal profit = storeService.calculateStoreProfit();

        // Correct expected values
        BigDecimal expectedRevenue = stokaFood.getPrice().multiply(new BigDecimal("10.0")); // 100.00
        BigDecimal expectedExpenses = cashier.getMonthlySalary()
                .add(stokaFood.getDeliveryPrice().multiply(new BigDecimal("10.0"))); // 1000 + 10 = 1010.00


    }

}
