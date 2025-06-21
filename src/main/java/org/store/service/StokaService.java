package org.store.service;

import org.store.data.Stoka;
import org.store.enums.StokaCategory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.stream.Collectors;

public class StokaService {
    



    public boolean isNearExpiration(Stoka stoka, int daysThreshold) {
        if (stoka.getExpirationDate() == null) {
            return false;
        }
        
        LocalDate today = LocalDate.now();
        LocalDate expirationDate = stoka.getExpirationDate();
        
        long daysUntilExpiration = ChronoUnit.DAYS.between(today, expirationDate);
        return daysUntilExpiration >= 0 && daysUntilExpiration <= daysThreshold;
    }
    



    public BigDecimal calculatePriceWithDiscount(Stoka stoka, int daysThreshold, double discountPercent) {
        if (isNearExpiration(stoka, daysThreshold)) {
            BigDecimal discount = stoka.getPrice().multiply(BigDecimal.valueOf(discountPercent / 100));
            return stoka.getPrice().subtract(discount);
        }
        return stoka.getPrice();
    }
    



    public List<Stoka> filterByCategory(List<Stoka> stokaList, StokaCategory category) {
        return stokaList.stream()
                .filter(stoka -> stoka.getStokaCategory() == category)
                .collect(Collectors.toList());
    }
    



    public List<Stoka> filterExpiredItems(List<Stoka> stokaList) {
        return stokaList.stream()
                .filter(Stoka::isExpired)
                .collect(Collectors.toList());
    }
    




    public List<Stoka> sortByPrice(List<Stoka> stokaList) {
        List<Stoka> sortedList = new ArrayList<>(stokaList);
        sortedList.sort((s1, s2) -> s1.getPrice().compareTo(s2.getPrice()));
        return sortedList;
    }
    

    public BigDecimal calculateTotalValue(Map<Stoka, Double> stokaQuantityMap) {
        BigDecimal total = BigDecimal.ZERO;
        
        for (Map.Entry<Stoka, Double> entry : stokaQuantityMap.entrySet()) {
            Stoka stoka = entry.getKey();
            Double quantity = entry.getValue();
            
            BigDecimal itemPrice = stoka.getPrice().multiply(BigDecimal.valueOf(quantity));
            total = total.add(itemPrice);
        }
        
        return total;
    }
    



    public Stoka updatePrice(Stoka stoka, BigDecimal newPrice) {
        if (newPrice.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Цената трябва да е положително число");
        }
        stoka.setPrice(newPrice);
        return stoka;
    }
    



    public Stoka updateDeliveryPrice(Stoka stoka, BigDecimal newDeliveryPrice) {
        if (newDeliveryPrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Цената на доставка не може да бъде отрицателна");
        }
        stoka.setDeliveryPrice(newDeliveryPrice);
        return stoka;
    }
    


    public Stoka extendExpirationDate(Stoka stoka, int days) {
        if (stoka.getExpirationDate() == null) {
            return stoka;
        }
        
        if (days <= 0) {
            throw new IllegalArgumentException("Дните за удължаване трябва да са положително число");
        }
        
        LocalDate newDate = stoka.getExpirationDate().plusDays(days);
        stoka.setExpirationDate(newDate);
        return stoka;
    }
}
