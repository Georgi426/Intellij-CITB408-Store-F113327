package org.store.service;

import org.store.data.Cashier;
import org.store.data.Stoka;
import org.store.data.Store;
import org.store.enums.StokaCategory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

public class StoreService {
    private Store store;

    public StoreService(Store store) {
        this.store = store;
    }

    public void addSoldStoka(Map<Stoka, Double> sold) {
        Map<Stoka, Double> tmpSoldStoka = this.store.getSoldStoka();

        for (Map.Entry<Stoka, Double> entry : sold.entrySet()) {
            Stoka stoka = entry.getKey();
            Double quantity = entry.getValue();

            Double currentQuantity = tmpSoldStoka.getOrDefault(stoka, 0.0);
            Double updatedQuantity = currentQuantity + quantity;

            tmpSoldStoka.put(stoka, updatedQuantity);
        }

        this.store.setSoldStoka(tmpSoldStoka);
    }

    private BigDecimal calculatePriceWithMargin(Stoka stoka) {
        BigDecimal currentPrice = stoka.getPrice();
        StokaCategory category = stoka.getStokaCategory();

        double margin = this.store.getMarginPercentByCategory().get(category);

        BigDecimal increase = currentPrice.multiply(new BigDecimal(margin / 100));
        BigDecimal updatedPrice = currentPrice.add(increase);

        return updatedPrice.setScale(2, BigDecimal.ROUND_UP);
    }

    /**
     * Проверява и коригира срока на годност на стоката преди доставка
     *
     * @param stoka стоката за проверка
     * @return true ако срокът е валиден, false ако не е
     */
    public boolean validateAndFixExpirationDate(Stoka stoka) {
        // Вземаме днешната дата
        LocalDate today = LocalDate.now();

        // Вземаме срока на годност на стоката
        LocalDate expirationDate = stoka.getExpirationDate();

        // Проверяваме дали срокът е в бъдещето
        if (expirationDate.isBefore(today) || expirationDate.isEqual(today)) {
            System.out.println("ГРЕШКА: Стоката " + stoka.getName() + " е с невалиден срок на годност!");
            System.out.println("Срок на годност: " + expirationDate);
            System.out.println("Днешна дата: " + today);

            // Задаваме нов срок на годност - 30 дни напред
            LocalDate newExpirationDate = today.plusDays(30);
            stoka.setExpirationDate(newExpirationDate);

            System.out.println("Автоматично коригиран срок на годност: " + newExpirationDate);
            return false;
        }

        // Срокът е валиден
        return true;
    }

    public void deliverStoka(Stoka stoka) {
        // Първо проверяваме и коригираме срока на годност
        boolean isExpirationDateValid = validateAndFixExpirationDate(stoka);

        if (!isExpirationDateValid) {
            System.out.println("Внимание: Срокът на годност на стоката беше автоматично коригиран!");
        }

        // Проверяваме дали стоката е валидна след корекцията
        if (stoka.isExpired()) {
            System.out.println("КРИТИЧНА ГРЕШКА: Стоката е с изтекъл срок и не може да бъде доставена!");
            return;
        }

        // First we need to calculate the new price with margin
        BigDecimal priceWithMargin = this.calculatePriceWithMargin(stoka);

        // Now we update the price of the item
        stoka.setPrice(priceWithMargin);

        // Now we need to add the item to the store's delivered items list
        this.store.addToDeliveredStoka(stoka);

        // And also add it to the store's inventory
        this.store.addToInventory(stoka);

//        // Let's print something so we know it worked!
//        System.out.println("!!! DELIVERED NEW ITEM !!!");
//        System.out.println("Item name: " + stoka.getName());
//        System.out.println("Item price: " + stoka.getPrice() + " lv");
//        System.out.println("Expiration date: " + stoka.getExpirationDate());
//        System.out.println("Is expired: " + (stoka.isExpired() ? "YES" : "NO"));
//        System.out.println("Current inventory count: " + this.store.getInventory().getOrDefault(stoka, 0.0));
//        System.out.println("------------------------");
    }

    public void removeFromInventory(Map<Stoka, Double> sold) {
        Map<Stoka, Double> tmpInventory = this.store.getInventory();

        for (Map.Entry<Stoka, Double> entry : sold.entrySet()) {
            Stoka stoka = entry.getKey();
            Double quantity = entry.getValue();

            Double currentQuantity = tmpInventory.getOrDefault(stoka, 0.0);
            Double updatedQuantity = currentQuantity - quantity;

            if (updatedQuantity == 0) {
                tmpInventory.remove(stoka);
            } else {
                tmpInventory.put(stoka, updatedQuantity);
            }
        }

        this.store.setInventory(tmpInventory);
    }

    public BigDecimal calculateStokaDeliveryExpenses() {
        BigDecimal result = BigDecimal.ZERO;
        for (Map.Entry<Stoka, Double> entry : this.store.getDeliveredStoka().entrySet()) {
            result = result.add(entry.getKey().getDeliveryPrice());
        }

        // Return the final result
        return result;
    }

    public BigDecimal calculateCashierSalaryExpenses() {
        BigDecimal salaries = BigDecimal.ZERO;

        for (Cashier cashier : this.store.getCashiers()) {
            salaries = salaries.add(cashier.getMonthlySalary());
        }

        return salaries;
    }

    public BigDecimal calculateStokaSoldRevenue() {
        // Initialize the result with zero
        BigDecimal result = new BigDecimal("0");

        // Get all sold items
        Map<Stoka, Double> allSoldItems = this.store.getSoldStoka();

        // Loop through each item one by one
        for (Map.Entry<Stoka, Double> entry : allSoldItems.entrySet()) {
            // Get the current item
            Stoka currentItem = entry.getKey();

            // Get how many of this item were sold
            Double howManySold = entry.getValue();

            // Convert the quantity to BigDecimal so we can do math
            BigDecimal quantityAsBigDecimal = new BigDecimal(howManySold.toString());

            // Get the price of the current item
            BigDecimal priceOfCurrentItem = currentItem.getPrice();

            // Calculate the total for this item by multiplying price and quantity
            BigDecimal totalForThisItem = priceOfCurrentItem.multiply(quantityAsBigDecimal);

            // Add this item's total to our running total
            result = result.add(totalForThisItem);

            // Print for debugging
            System.out.println("Item: " + currentItem.getName() +
                    ", Quantity: " + howManySold +
                    ", Price: " + priceOfCurrentItem +
                    ", Total: " + totalForThisItem);
        }

        // Return the final result
        return result;
    }

    public BigDecimal calculateStoreProfit() {
        BigDecimal salaries = this.calculateCashierSalaryExpenses();
        BigDecimal deliveries = this.calculateStokaDeliveryExpenses();
        BigDecimal revenue = this.calculateStokaSoldRevenue();

        BigDecimal result = revenue.subtract(salaries.add(deliveries));

        return result;
    }

    public BigDecimal deliverStokaReturnPriceWithMargin(Stoka stokaFood) {

        return null;
    }


}
