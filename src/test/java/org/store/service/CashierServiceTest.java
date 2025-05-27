package org.store.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.store.data.Cashier;
import org.store.data.Receipt;
import org.store.data.Stoka;
import org.store.enums.StokaCategory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class CashierServiceTest {

    private CashierService cashierService;
    private Cashier cashier;
    private Receipt receipt1;
    private Receipt receipt2;
    private Stoka stoka1;
    private Stoka stoka2;

    @BeforeEach
    void setUp() {
        // Initialize service
        cashierService = new CashierService();
        
        // Create test cashier
        cashier = new Cashier("Test Cashier", new BigDecimal("2000.00"));
        
        // Create test products
        stoka1 = new Stoka("001", "Test Product 1", 
                           new BigDecimal("10.00"), new BigDecimal("20.00"), 
                           LocalDate.now().plusDays(30), StokaCategory.FOOD);
        
        stoka2 = new Stoka("002", "Test Product 2", 
                           new BigDecimal("15.00"), new BigDecimal("30.00"), 
                           LocalDate.now().plusDays(60), StokaCategory.NONFOOD);
        
        // Create test receipts
        Map<Stoka, Double> items1 = new HashMap<>();
        items1.put(stoka1, 2.0); // 2 * 20.00 = 40.00
        receipt1 = new Receipt("R001", cashier, LocalDate.now(), items1);
        
        Map<Stoka, Double> items2 = new HashMap<>();
        items2.put(stoka2, 3.0); // 3 * 30.00 = 90.00
        receipt2 = new Receipt("R002", cashier, LocalDate.now().minusDays(1), items2);
    }

    @Test
    void registerReceipt_ShouldAddReceiptToCashier() {
        // When
        cashierService.registerReceipt(cashier, receipt1);
        
        // Then
        List<Receipt> receipts = cashierService.getCashierReceipts(cashier);
        assertEquals(1, receipts.size());
        assertSame(receipt1, receipts.get(0));
    }

    @Test
    void getCashierReceipts_ShouldReturnEmptyList_WhenNoneRegistered() {
        // When
        List<Receipt> receipts = cashierService.getCashierReceipts(cashier);
        
        // Then
        assertNotNull(receipts);
        assertTrue(receipts.isEmpty());
    }

    @Test
    void getCashierReceipts_ShouldReturnAllReceipts_WhenMultipleRegistered() {
        // Given
        cashierService.registerReceipt(cashier, receipt1);
        cashierService.registerReceipt(cashier, receipt2);
        
        // When
        List<Receipt> receipts = cashierService.getCashierReceipts(cashier);
        
        // Then
        assertEquals(2, receipts.size());
        assertTrue(receipts.contains(receipt1));
        assertTrue(receipts.contains(receipt2));
    }

    @Test
    void calculateTotalSales_ShouldReturnZero_WhenNoReceipts() {
        // When
        BigDecimal totalSales = cashierService.calculateTotalSales(cashier);
        
        // Then
        assertEquals(BigDecimal.ZERO, totalSales);
    }

    @Test
    void calculateTotalSales_ShouldReturnCorrectSum_WhenReceiptsExist() {
        // Given
        cashierService.registerReceipt(cashier, receipt1); // 40.00
        cashierService.registerReceipt(cashier, receipt2); // 90.00
        
        // When
        BigDecimal totalSales = cashierService.calculateTotalSales(cashier);
        
        // Then
        assertEquals(new BigDecimal("130.00"), totalSales);
    }

    @Test
    void calculateBonus_ShouldReturnCorrectAmount_WithPercentage() {
        // Given
        cashierService.registerReceipt(cashier, receipt1); // 40.00
        cashierService.registerReceipt(cashier, receipt2); // 90.00
        // Total sales: 130.00
        
        // When
        BigDecimal bonus = cashierService.calculateBonus(cashier, 10.0); // 10% of 130.00 = 13.00
        
        // Then
        assertEquals(new BigDecimal("13.00"), bonus);
    }

    @Test
    void calculateTotalSalary_ShouldCombineBaseSalaryAndBonus() {
        // Given
        cashierService.registerReceipt(cashier, receipt1); // 40.00
        cashierService.registerReceipt(cashier, receipt2); // 90.00
        // Total sales: 130.00
        
        // When
        BigDecimal totalSalary = cashierService.calculateTotalSalary(cashier, 10.0);
        // Base salary: 2000.00, Bonus: 13.00
        
        // Then
        assertEquals(new BigDecimal("2013.00"), totalSalary);
    }

    @Test
    void getReceiptsByDate_ShouldFilterByDate() {
        // Given
        cashierService.registerReceipt(cashier, receipt1); // Today
        cashierService.registerReceipt(cashier, receipt2); // Yesterday
        
        // When
        List<Receipt> todayReceipts = cashierService.getReceiptsByDate(cashier, LocalDate.now());
        
        // Then
        assertEquals(1, todayReceipts.size());
        assertSame(receipt1, todayReceipts.get(0));
    }

    @Test
    void calculateAverageReceiptValue_ShouldReturnAverage() {
        // Given
        cashierService.registerReceipt(cashier, receipt1); // 40.00
        cashierService.registerReceipt(cashier, receipt2); // 90.00
        // Average: (40.00 + 90.00) / 2 = 65.00
        
        // When
        BigDecimal average = cashierService.calculateAverageReceiptValue(cashier);
        
        // Then
        assertEquals(new BigDecimal("65.00"), average);
    }

    @Test
    void getReceiptCount_ShouldReturnCorrectCount() {
        // Given
        cashierService.registerReceipt(cashier, receipt1);
        cashierService.registerReceipt(cashier, receipt2);
        
        // When
        int count = cashierService.getReceiptCount(cashier);
        
        // Then
        assertEquals(2, count);
    }

    @Test
    void calculatePerformance_ShouldReturnSalesPerHour() {
        // Given
        cashierService.registerReceipt(cashier, receipt1); // 40.00
        cashierService.registerReceipt(cashier, receipt2); // 90.00
        // Total sales: 130.00
        
        // When
        BigDecimal performance = cashierService.calculatePerformance(cashier, 5); // 5 hours
        // Expected: 130.00 / 5 = 26.00
        
        // Then
        assertEquals(new BigDecimal("26.00"), performance);
    }

    @Test
    void calculatePerformance_ShouldThrowException_WhenHoursIsZeroOrNegative() {
        // Given
        cashierService.registerReceipt(cashier, receipt1);
        
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> cashierService.calculatePerformance(cashier, 0));
        assertThrows(IllegalArgumentException.class, () -> cashierService.calculatePerformance(cashier, -1));
    }

    @Test
    void calculateIncomeTax_ShouldReturnCorrectTaxAmount() {
        // Given
        // Monthly salary: 2000.00
        
        // When
        BigDecimal tax = cashierService.calculateIncomeTax(cashier, 10.0); // 10% of 2000.00 = 200.00
        
        // Then
        assertEquals(new BigDecimal("200.00"), tax);
    }
}
