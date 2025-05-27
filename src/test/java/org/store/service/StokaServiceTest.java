package org.store.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.store.data.Stoka;
import org.store.enums.StokaCategory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class StokaServiceTest {

    private StokaService stokaService;
    private Stoka stokaExpiringSoon;
    private Stoka stokaExpired;
    private Stoka stokaFresh;
    private Stoka stokaNonFood;
    private List<Stoka> stokaList;

    @BeforeEach
    void setUp() {
        // Initialize service
        stokaService = new StokaService();
        
        // Create test products
        stokaExpiringSoon = new Stoka("001", "Milk", 
                                     new BigDecimal("2.00"), new BigDecimal("3.50"), 
                                     LocalDate.now().plusDays(3), StokaCategory.FOOD);
        
        stokaExpired = new Stoka("002", "Yogurt", 
                                new BigDecimal("1.50"), new BigDecimal("2.80"), 
                                LocalDate.now().minusDays(1), StokaCategory.FOOD);
        
        stokaFresh = new Stoka("003", "Cheese", 
                              new BigDecimal("5.00"), new BigDecimal("8.90"), 
                              LocalDate.now().plusDays(30), StokaCategory.FOOD);
        
        stokaNonFood = new Stoka("004", "Detergent", 
                                new BigDecimal("4.00"), new BigDecimal("6.50"), 
                                null, StokaCategory.NONFOOD);
        
        // Create list of products
        stokaList = new ArrayList<>();
        stokaList.add(stokaExpiringSoon);
        stokaList.add(stokaExpired);
        stokaList.add(stokaFresh);
        stokaList.add(stokaNonFood);
    }

    @Test
    void isNearExpiration_ShouldReturnTrue_WhenProductIsCloseToExpiring() {
        // When
        boolean result = stokaService.isNearExpiration(stokaExpiringSoon, 5);
        
        // Then
        assertTrue(result);
    }

    @Test
    void isNearExpiration_ShouldReturnFalse_WhenProductIsNotCloseToExpiring() {
        // When
        boolean result = stokaService.isNearExpiration(stokaFresh, 5);
        
        // Then
        assertFalse(result);
    }

    @Test
    void isNearExpiration_ShouldReturnFalse_WhenProductIsNonFood() {
        // When
        boolean result = stokaService.isNearExpiration(stokaNonFood, 5);
        
        // Then
        assertFalse(result);
    }



    @Test
    void calculatePriceWithDiscount_ShouldNotApplyDiscount_WhenProductIsNotNearExpiration() {
        // Given
        BigDecimal originalPrice = stokaFresh.getPrice(); // 8.90
        
        // When
        BigDecimal result = stokaService.calculatePriceWithDiscount(stokaFresh, 5, 10.0);
        
        // Then
        assertEquals(originalPrice, result);
    }

    @Test
    void filterByCategory_ShouldReturnOnlyFoodItems() {
        // When
        List<Stoka> result = stokaService.filterByCategory(stokaList, StokaCategory.FOOD);
        
        // Then
        assertEquals(3, result.size());
        assertTrue(result.contains(stokaExpiringSoon));
        assertTrue(result.contains(stokaExpired));
        assertTrue(result.contains(stokaFresh));
        assertFalse(result.contains(stokaNonFood));
    }

    @Test
    void filterByCategory_ShouldReturnOnlyNonFoodItems() {
        // When
        List<Stoka> result = stokaService.filterByCategory(stokaList, StokaCategory.NONFOOD);
        
        // Then
        assertEquals(1, result.size());
        assertTrue(result.contains(stokaNonFood));
    }

//    @Test
//    void filterExpiredItems_ShouldReturnOnlyExpiredItems() {
//        // When
//        List<Stoka> result = stokaService.filterExpiredItems(stokaList);
//
//        // Then
//        assertEquals(1, result.size());
//        assertTrue(result.contains(stokaExpired));
//    }

    @Test
    void sortByPrice_ShouldReturnItemsSortedByPriceAscending() {
        // When
        List<Stoka> result = stokaService.sortByPrice(stokaList);
        
        // Then
        assertEquals(4, result.size());
        assertEquals(stokaExpired, result.get(0)); // 2.80
        assertEquals(stokaExpiringSoon, result.get(1)); // 3.50
        assertEquals(stokaNonFood, result.get(2)); // 6.50
        assertEquals(stokaFresh, result.get(3)); // 8.90
    }

//    @Test
//    void calculateTotalValue_ShouldReturnCorrectSum() {
//        // Given
//        Map<Stoka, Double> stokaQuantityMap = new HashMap<>();
//        stokaQuantityMap.put(stokaExpiringSoon, 2.0); // 3.50 * 2 = 7.00
//        stokaQuantityMap.put(stokaFresh, 1.0); // 8.90 * 1 = 8.90
//        // Total expected: 15.90
//
//        // When
//        BigDecimal result = stokaService.calculateTotalValue(stokaQuantityMap);
//
//        // Then
//        assertEquals(new BigDecimal("15.90"), result);
//    }

    @Test
    void updatePrice_ShouldChangePrice() {
        // Given
        BigDecimal newPrice = new BigDecimal("4.99");
        
        // When
        Stoka result = stokaService.updatePrice(stokaExpiringSoon, newPrice);
        
        // Then
        assertEquals(newPrice, result.getPrice());
        assertSame(stokaExpiringSoon, result);
    }

    @Test
    void updatePrice_ShouldThrowException_WhenPriceIsNegative() {
        // Given
        BigDecimal negativePrice = new BigDecimal("-1.00");
        
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> 
            stokaService.updatePrice(stokaExpiringSoon, negativePrice));
    }

    @Test
    void updateDeliveryPrice_ShouldChangeDeliveryPrice() {
        // Given
        BigDecimal newDeliveryPrice = new BigDecimal("3.25");
        
        // When
        Stoka result = stokaService.updateDeliveryPrice(stokaExpiringSoon, newDeliveryPrice);
        
        // Then
        assertEquals(newDeliveryPrice, result.getDeliveryPrice());
        assertSame(stokaExpiringSoon, result);
    }

    @Test
    void updateDeliveryPrice_ShouldThrowException_WhenPriceIsNegative() {
        // Given
        BigDecimal negativePrice = new BigDecimal("-1.00");
        
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> 
            stokaService.updateDeliveryPrice(stokaExpiringSoon, negativePrice));
    }

    @Test
    void extendExpirationDate_ShouldAddDaysToExpirationDate() {
        // Given
        LocalDate originalDate = stokaExpiringSoon.getExpirationDate();
        int daysToAdd = 10;
        LocalDate expectedDate = originalDate.plusDays(daysToAdd);
        
        // When
        Stoka result = stokaService.extendExpirationDate(stokaExpiringSoon, daysToAdd);
        
        // Then
        assertEquals(expectedDate, result.getExpirationDate());
        assertSame(stokaExpiringSoon, result);
    }

    @Test
    void extendExpirationDate_ShouldNotChangeDate_WhenItemIsNonFood() {
        // When
        Stoka result = stokaService.extendExpirationDate(stokaNonFood, 10);
        
        // Then
        assertNull(result.getExpirationDate());
        assertSame(stokaNonFood, result);
    }

    @Test
    void extendExpirationDate_ShouldThrowException_WhenDaysIsNegative() {
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> 
            stokaService.extendExpirationDate(stokaExpiringSoon, -5));
    }
}
