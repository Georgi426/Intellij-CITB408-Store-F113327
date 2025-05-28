package org.store.service;

import org.store.data.Stoka;
import org.store.data.Receipt;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class ReceiptService {
    private Receipt receipt;
    private static final AtomicInteger receiptCounter = new AtomicInteger(0);

    public ReceiptService(Receipt receipt) {
        this.receipt = receipt;

        // Проверка дали бележката има поне една стока


        if (receipt != null && receipt.getStoka() != null && !receipt.getStoka().isEmpty()) {
            receiptCounter.incrementAndGet();
        } else {
            System.out.println("⚠️ ПРЕДУПРЕЖДЕНИЕ: Празна касова бележка - не е отчетена в броя.");
        }
    }

    public BigDecimal calculateTotalPrice() {
        Map<Stoka, Double> items = this.receipt.getStoka();

        if (items == null || items.isEmpty()) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.UP);
        }

        BigDecimal total = BigDecimal.ZERO;

        for (Map.Entry<Stoka, Double> entry : items.entrySet()) {
            Stoka stoka = entry.getKey();
            Double quantity = entry.getValue();

            if (stoka != null && quantity != null && quantity > 0) {
                BigDecimal price = stoka.getPrice();
                BigDecimal qty = BigDecimal.valueOf(quantity);
                BigDecimal itemTotal = price.multiply(qty);
                total = total.add(itemTotal);
            }
        }

        return total.setScale(2, RoundingMode.UP);
    }

    public int countReceiptsIssued() {
        return receiptCounter.get();
    }

    public String getFormattedReceipt() {
        StringBuilder sb = new StringBuilder();

        if (receipt == null || receipt.getStoka() == null || receipt.getStoka().isEmpty()) {
            return " ГРЕШКА: Опит за печат на празна или невалидна касова бележка!";
        }

        sb.append("===== КАСОВА БЕЛЕЖКА =====\n");
        sb.append("Номер на бележка: ").append(receipt.getSerialNumber()).append("\n");
        sb.append("Касиер: ").append(receipt.getCashier().getName()).append("\n");
        sb.append("Дата: ").append(receipt.getIssueDate()).append("\n");
        sb.append("----------------------\n");
        sb.append("Артикули:\n");

        for (Map.Entry<Stoka, Double> entry : receipt.getStoka().entrySet()) {
            Stoka stoka = entry.getKey();
            Double quantity = entry.getValue();
            BigDecimal price = stoka.getPrice();
            BigDecimal qty = BigDecimal.valueOf(quantity);
            BigDecimal itemTotal = price.multiply(qty);

            sb.append("  ").append(stoka.getName())
                    .append(" - ").append(quantity).append(" бр. x ")
                    .append(price).append(" лв. = ")
                    .append(itemTotal.setScale(2, RoundingMode.UP)).append(" лв.\n");
        }

        sb.append("----------------------\n");
        sb.append("Общо: ").append(calculateTotalPrice()).append(" лв.\n");
        sb.append("==========================\n");

        return sb.toString();
    }

}
