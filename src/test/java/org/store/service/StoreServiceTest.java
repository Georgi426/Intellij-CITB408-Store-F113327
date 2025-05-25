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

    /**
     * Тестът проверява правилното изчисляване на цена с надценка за различни категории стоки.
     * Тестват се две различни категории - хранителни и нехранителни продукти, 
     * като се проверява дали се прилага правилният процент надценка за всяка категория.
     */
    @Test
    void testCalculatePriceWithMargin() {
        BigDecimal newPriceFood = storeService.deliverStokaReturnPriceWithMargin(stokaFood);
        assertEquals(new BigDecimal("5.50"), newPriceFood);  // 5 + 10%

        BigDecimal newPriceNonFood = storeService.deliverStokaReturnPriceWithMargin(stokaNonFood);
        assertEquals(new BigDecimal("17.25"), newPriceNonFood); // 15 + 15%
    }

    /**
     * Тестът проверява функционалността за валидиране и коригиране на срока на годност на стоките.
     * Проверява се обработката на два случая:
     * 1. Продукт с изтекъл срок на годност - очаква се коригиране на датата и връщане на false
     * 2. Продукт с валиден срок на годност - очаква се връщане на true без промяна на датата
     */
    @Test
    void testValidateAndFixExpirationDate() {
        // Expired product
        Stoka expired = new Stoka("003", "Стар продукт", new BigDecimal("10"), new BigDecimal("12"), LocalDate.now().minusDays(1), StokaCategory.FOOD);
        boolean valid = storeService.validateAndFixExpirationDate(expired);
        assertFalse(valid);
        assertTrue(expired.getExpirationDate().isAfter(LocalDate.now()));

        // Valid product
        boolean valid2 = storeService.validateAndFixExpirationDate(stokaFood);
        assertTrue(valid2);
    }

    /**
     * Тестът проверява дали продадените стоки се добавят правилно към списъка с продажби на магазина.
     * Проверява се дали стоката с правилното количество се записва в картата на продадените стоки.
     */
    @Test
    void testAddSoldStoka() {
        Map<Stoka, Double> soldMap = new HashMap<>();
        soldMap.put(stokaFood, 3.0);
        storeService.addSoldStoka(soldMap);

        assertEquals(3.0, store.getSoldStoka().get(stokaFood));
    }

    /**
     * Тестът проверява дали методът правилно намалява количеството на стоки в инвентара
     * при продажба. Първо се добавят 3 единици от продукт в инвентара, след което се
     * симулира продажба на 2 единици и се проверява дали в инвентара остава 1 единица.
     */
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

    /**
     * Тестът проверява правилното изчисляване на печалбата на магазина.
     * Симулира се работна среда с:
     * 1. Добавен касиер с месечна заплата
     * 2. Доставена стока с цена на доставка
     * 3. Продадена стока с определено количество
     * 
     * Печалбата се изчислява като разлика между приходите от продажби и разходите
     * (заплати на касиери + разходи за доставка).
     */
    @Test
    void testCalculateStoreProfit() {
        // Add some cashiers
        Cashier cashier = new Cashier("Ben", new BigDecimal("2000"));
        store.addCashier(cashier);

        // Add delivered stoka with delivery price
        stokaFood.setDeliveryPrice(new BigDecimal("1.00"));
        store.addToDeliveredStoka(stokaFood);

        // Add sold stoka
        Map<Stoka, Double> soldMap = new HashMap<>();
        soldMap.put(stokaFood, 10.0);
        store.setSoldStoka(soldMap);

        BigDecimal profit = storeService.calculateStoreProfit();

        BigDecimal expectedRevenue = stokaFood.getPrice().multiply(new BigDecimal("10.0"));
        BigDecimal expectedExpenses = cashier.getMonthlySalary().add(stokaFood.getDeliveryPrice());

        assertEquals(expectedRevenue.subtract(expectedExpenses), profit);
    }
}
