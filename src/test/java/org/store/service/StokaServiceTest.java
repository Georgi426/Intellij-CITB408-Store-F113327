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

        stokaService = new StokaService();


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


        stokaList = new ArrayList<>();
        stokaList.add(stokaExpiringSoon);
        stokaList.add(stokaExpired);
        stokaList.add(stokaFresh);
        stokaList.add(stokaNonFood);
    }

    @Test
    void isNearExpiration_ShouldReturnTrue_WhenProductIsCloseToExpiring() {

        boolean result = stokaService.isNearExpiration(stokaExpiringSoon, 5);


        assertTrue(result);
    }

    @Test
    void isNearExpiration_ShouldReturnFalse_WhenProductIsNotCloseToExpiring() {

        boolean result = stokaService.isNearExpiration(stokaFresh, 5);


        assertFalse(result);
    }

    @Test
    void isNearExpiration_ShouldReturnFalse_WhenProductIsNonFood() {

        boolean result = stokaService.isNearExpiration(stokaNonFood, 5);


        assertFalse(result);
    }


    @Test
    void calculatePriceWithDiscount_ShouldNotApplyDiscount_WhenProductIsNotNearExpiration() {

        BigDecimal originalPrice = stokaFresh.getPrice(); // 8.90


        BigDecimal result = stokaService.calculatePriceWithDiscount(stokaFresh, 5, 10.0);


        assertEquals(originalPrice, result);
    }

    @Test
    void filterByCategory_ShouldReturnOnlyFoodItems() {

        List<Stoka> result = stokaService.filterByCategory(stokaList, StokaCategory.FOOD);


        assertEquals(3, result.size());
        assertTrue(result.contains(stokaExpiringSoon));
        assertTrue(result.contains(stokaExpired));
        assertTrue(result.contains(stokaFresh));
        assertFalse(result.contains(stokaNonFood));
    }
}