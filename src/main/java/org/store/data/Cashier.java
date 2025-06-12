package org.store.data;

import java.math.BigDecimal;
import java.util.UUID;

public class Cashier {
    private  UUID id;
    private  String name;
    private  BigDecimal monthlySalary;

    public Cashier(String name, BigDecimal monthlySalary) {

        this.id = UUID.randomUUID();
        this.name = name;
        this.monthlySalary = monthlySalary;
    }

    public UUID getId() {

        return id;
    }

    public String getName() {

        return name;
    }

    public BigDecimal getMonthlySalary() {

        return monthlySalary;
    }

}
