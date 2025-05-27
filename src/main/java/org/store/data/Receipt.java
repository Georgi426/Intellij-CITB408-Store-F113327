package org.store.data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

public class Receipt {
    private String serialNumber;
    private Cashier cashier;
    private LocalDate issueDate;
    private Map<Stoka, Double> stoka;

    public Receipt(String serialNumber, Cashier cashier, LocalDate issueDate, Map<Stoka, Double> stoka) {
        this.serialNumber = serialNumber;
        this.cashier = cashier;
        this.issueDate = issueDate;
        this.stoka = stoka;
    }

    public Receipt(int i, Cashier cashier, Map<Stoka, Double> stoki, LocalDateTime now) {
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public Cashier getCashier() {
        return cashier;
    }

    public void setCashier(Cashier cashier) {
        this.cashier = cashier;
    }

    public LocalDate getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(LocalDate issueDate) {
        this.issueDate = issueDate;
    }

    public Map<Stoka, Double> getStoka() {
        return stoka;
    }

    public void setStoka(Map<Stoka, Double> stoka) {
        this.stoka = stoka;
    }
}


