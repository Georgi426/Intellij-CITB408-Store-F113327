package org.store.service;

import org.store.data.Stoka;
import org.store.data.Receipt;

import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;


public class ReceiptService {
    private Receipt receipt;
    private static AtomicInteger receiptCounter = new AtomicInteger(0);

    public ReceiptService(Receipt receipt) {
        this.receipt = receipt;
        receiptCounter.incrementAndGet();
    }

    public BigDecimal calculateTotalPrice() {
        Map<Stoka, Double> items = this.receipt.getStoka();
        
        if (items.isEmpty()) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.UP);
        }
        
        BigDecimal total = BigDecimal.ZERO;
        
        for (Map.Entry<Stoka, Double> entry : items.entrySet()) {
            Stoka stoka = entry.getKey();
            Double quantity = entry.getValue();
            
            BigDecimal price = stoka.getPrice();
            BigDecimal quantityAsBigDecimal = new BigDecimal(quantity.toString());
            
            BigDecimal itemTotal = price.multiply(quantityAsBigDecimal);
            total = total.add(itemTotal);
        }
        
        return total.setScale(2, RoundingMode.UP);
    }
    
    public int countReceiptsIssued() {
        return receiptCounter.get();
    }
    
    public String getFormattedReceipt() {
        StringBuilder sb = new StringBuilder();
        
        sb.append("===== RECEIPT =====\n");
        sb.append("Receipt Number: ").append(this.receipt.getSerialNumber()).append("\n");
        sb.append("Cashier: ").append(this.receipt.getCashier().getName()).append("\n");
        sb.append("Date: ").append(this.receipt.getIssueDate()).append("\n");
        sb.append("------------------\n");
        
        sb.append("Items:\n");
        Map<Stoka, Double> items = this.receipt.getStoka();
        for (Map.Entry<Stoka, Double> entry : items.entrySet()) {
            Stoka stoka = entry.getKey();
            Double quantity = entry.getValue();
            
            BigDecimal price = stoka.getPrice();
            BigDecimal quantityAsBigDecimal = new BigDecimal(quantity.toString());
            BigDecimal itemTotal = price.multiply(quantityAsBigDecimal);
            
            sb.append("  ").append(stoka.getName())
              .append(" - ").append(quantity)
              .append(" x ").append(price)
              .append(" = ").append(itemTotal).append("\n");
        }
        
        sb.append("------------------\n");
        sb.append("Total: ").append(calculateTotalPrice()).append("\n");
        sb.append("===================\n");
        
        return sb.toString();
    }
}


