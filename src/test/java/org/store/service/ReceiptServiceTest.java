package org.store.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.store.data.Cashier;
import org.store.data.Receipt;
import org.store.data.Stoka;
import org.store.enums.StokaCategory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ReceiptServiceTest {
    private Stoka stoka1;
    private Stoka stoka2;
    private Cashier cashier;
    private Map<Stoka, Double> stoki;
    private ReceiptService receiptService;

    @BeforeEach
    void setUp() {
        stoka1 = new Stoka("001", "Хляб", new BigDecimal("2.00"), new BigDecimal("2.50"),
                LocalDate.now().plusDays(3), StokaCategory.FOOD);
        stoka2 = new Stoka("002", "Сапун", new BigDecimal("1.50"), new BigDecimal("2.00"),
                LocalDate.now().plusDays(7), StokaCategory.NONFOOD);

        cashier = new Cashier("Иван", new BigDecimal("1000"));

        stoki = new HashMap<>();
        stoki.put(stoka1, 2.0); // 2 * 2.00 = 4.00
        stoki.put(stoka2, 1.0); // 1 * 1.50 = 1.50

        Receipt receipt = new Receipt(1, cashier, stoki, LocalDateTime.now());
        receiptService = new ReceiptService(receipt);
    }



    @Test
    void testCalculateTotalPrice_EmptyMap() {
        Receipt emptyReceipt = new Receipt(2, cashier, new HashMap<>(), LocalDateTime.now());
        ReceiptService service = new ReceiptService(emptyReceipt);

        assertEquals(new BigDecimal("0.00"), service.calculateTotalPrice());
    }

    @Test
    void testCalculateTotalPrice_NullStokaMap() {
        Receipt nullStokaReceipt = new Receipt(3, cashier, null, LocalDateTime.now());
        ReceiptService service = new ReceiptService(nullStokaReceipt);

        assertEquals(new BigDecimal("0.00"), service.calculateTotalPrice());
    }



    @Test
    void testFormattedReceipt_WithEmptyItems_ReturnsWarning() {
        Receipt emptyReceipt = new Receipt(4, cashier, new HashMap<>(), LocalDateTime.now());
        ReceiptService service = new ReceiptService(emptyReceipt);

        String output = service.getFormattedReceipt();
        assertTrue(output.contains("ГРЕШКА"));
    }

    @Test
    void testCountReceiptsIssued_IncrementsOnlyIfNotEmpty() {
        int countBefore = receiptService.countReceiptsIssued();

        // This should not increment the count (empty stoka map)
        Receipt empty = new Receipt(5, cashier, new HashMap<>(), LocalDateTime.now());
        new ReceiptService(empty);

        // This should increment
        Map<Stoka, Double> map = new HashMap<>();
        map.put(stoka1, 1.0);
        Receipt valid = new Receipt(6, cashier, map, LocalDateTime.now());
        new ReceiptService(valid);

        int countAfter = receiptService.countReceiptsIssued();
        assertEquals(countBefore + 1, countAfter);
    }


}
