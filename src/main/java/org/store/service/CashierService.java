package org.store.service;

import org.store.data.Cashier;
import org.store.data.Receipt;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CashierService {
    private Map<String, List<Receipt>> cashierReceipts;
    
    public CashierService() {
        this.cashierReceipts = new HashMap<>();
    }
    
//
//    Регистрира касова бележка към касиер

    public void registerReceipt(Cashier cashier, Receipt receipt) {
        String cashierId = cashier.getId();
        
        if (!cashierReceipts.containsKey(cashierId)) {
            cashierReceipts.put(cashierId, new ArrayList<>());
        }
        
        cashierReceipts.get(cashierId).add(receipt);
    }
    
//
//      Връща всички бележки, издадени от касиер
//
//      return списък с бележки
//
    public List<Receipt> getCashierReceipts(Cashier cashier) {
        String cashierId = cashier.getId();
        return cashierReceipts.getOrDefault(cashierId, new ArrayList<>());
    }
    
//
//     Изчислява общата стойност на продажбите на касиер
//
//     return общата стойност
//
    public BigDecimal calculateTotalSales(Cashier cashier) {
        List<Receipt> receipts = getCashierReceipts(cashier);
        BigDecimal total = BigDecimal.ZERO;
        
        for (Receipt receipt : receipts) {
            ReceiptService receiptService = new ReceiptService(receipt);
            total = total.add(receiptService.calculateTotalPrice());
        }
        
        return total;
    }
    
//
//     Изчислява бонус за касиер, базиран на продажбите
//
//
//      return сума на бонуса
//

    public BigDecimal calculateBonus(Cashier cashier, double percentOfSales) {
        BigDecimal totalSales = calculateTotalSales(cashier);
        return totalSales.multiply(BigDecimal.valueOf(percentOfSales / 100))
                .setScale(2, RoundingMode.HALF_UP);
    }
    
//
//      Изчислява общата месечна заплата на касиер, включително бонуси
//
//      return обща заплата
//
    public BigDecimal calculateTotalSalary(Cashier cashier, double percentOfSales) {
        BigDecimal baseSalary = cashier.getMonthlySalary();
        BigDecimal bonus = calculateBonus(cashier, percentOfSales);
        
        return baseSalary.add(bonus).setScale(2, RoundingMode.HALF_UP);
    }
    
//
//      Връща бележките, издадени от касиер в определен ден
//
//      return списък с бележки
//
    public List<Receipt> getReceiptsByDate(Cashier cashier, LocalDate date) {
        List<Receipt> cashierReceipts = getCashierReceipts(cashier);
        List<Receipt> filteredReceipts = new ArrayList<>();
        
        for (Receipt receipt : cashierReceipts) {
            if (receipt.getIssueDate().equals(date)) {
                filteredReceipts.add(receipt);
            }
        }
        
        return filteredReceipts;
    }
    
//
//     Изчислява средна стойност на бележка за касиер
//
//     return средна стойност
//
    public BigDecimal calculateAverageReceiptValue(Cashier cashier) {
        List<Receipt> receipts = getCashierReceipts(cashier);
        
        if (receipts.isEmpty()) {
            return BigDecimal.ZERO;
        }
        
        BigDecimal totalValue = calculateTotalSales(cashier);
        return totalValue.divide(BigDecimal.valueOf(receipts.size()), 2, RoundingMode.HALF_UP);
    }
    
//
//      Връща брой бележки, издадени от касиер
//      return брой бележки
//
    public int getReceiptCount(Cashier cashier) {
        return getCashierReceipts(cashier).size();
    }
    
//
//      Изчислява производителност на касиер (продажби на час)
//      hoursWorked отработени часове
//      return производителност
//
    public BigDecimal calculatePerformance(Cashier cashier, int hoursWorked) {
        if (hoursWorked <= 0) {
            throw new IllegalArgumentException("Часовете трябва да са положително число");
        }
        
        BigDecimal totalSales = calculateTotalSales(cashier);
        return totalSales.divide(BigDecimal.valueOf(hoursWorked), 2, RoundingMode.HALF_UP);
    }
    
//
//      Изчислява данък върху заплатата на касиер
//      taxRate данъчна ставка (в проценти)
//      return сума на данъка
//

    public BigDecimal calculateIncomeTax(Cashier cashier, double taxRate) {
        BigDecimal salary = cashier.getMonthlySalary();
        return salary.multiply(BigDecimal.valueOf(taxRate / 100))
                .setScale(2, RoundingMode.HALF_UP);
    }
}
