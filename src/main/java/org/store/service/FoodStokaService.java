package org.store.service;

import org.store.data.FoodStoka;
import org.store.data.Stoka;
import org.store.enums.StokaCategory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FoodStokaService {
    
    public FoodStoka createFoodStoka(String id, String name, BigDecimal deliveryPrice, 
                                      BigDecimal price, LocalDate expirationDate) {
        if (expirationDate == null || expirationDate.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Срокът на годност трябва да е валидна бъдеща дата");
        }
        
        return new FoodStoka(id, name, deliveryPrice, price, expirationDate);
    }
    
    
    public boolean isNearExpiration(FoodStoka foodStoka, int daysThreshold) {
        LocalDate today = LocalDate.now();
        LocalDate expirationDate = foodStoka.getExpirationDate();
        
        if (expirationDate == null) {
            return false;
        }
        
        long daysUntilExpiration = ChronoUnit.DAYS.between(today, expirationDate);
        return daysUntilExpiration >= 0 && daysUntilExpiration <= daysThreshold;
    }
    
    
    public FoodStoka applyExpirationDiscount(FoodStoka foodStoka, int daysThreshold, double discountPercent) {
        if (isNearExpiration(foodStoka, daysThreshold)) {
            BigDecimal currentPrice = foodStoka.getPrice();
            BigDecimal discountAmount = currentPrice.multiply(BigDecimal.valueOf(discountPercent / 100));
            BigDecimal newPrice = currentPrice.subtract(discountAmount);
            
            foodStoka.setPrice(newPrice);
            return foodStoka;
        }
        
        return foodStoka;
    }
    
    
    public List<FoodStoka> filterFoodItems(List<Stoka> stokaList) {
        return stokaList.stream()
                .filter(stoka -> stoka.getStokaCategory() == StokaCategory.FOOD)
                .filter(stoka -> stoka instanceof FoodStoka)
                .map(stoka -> (FoodStoka) stoka)
                .collect(Collectors.toList());
    }
    
    
    public List<FoodStoka> filterExpiredFoodItems(List<FoodStoka> foodStokaList) {
        return foodStokaList.stream()
                .filter(FoodStoka::isExpired)
                .collect(Collectors.toList());
    }
    
    
    public List<FoodStoka> filterNearExpirationFoodItems(List<FoodStoka> foodStokaList, int daysThreshold) {
        return foodStokaList.stream()
                .filter(foodStoka -> isNearExpiration(foodStoka, daysThreshold))
                .collect(Collectors.toList());
    }
    
    
    public List<FoodStoka> sortByExpirationDate(List<FoodStoka> foodStokaList) {
        List<FoodStoka> sortedList = new ArrayList<>(foodStokaList);
        sortedList.sort(Comparator.comparing(FoodStoka::getExpirationDate));
        return sortedList;
    }
    
    
    public double calculateFreshnessPercentage(FoodStoka foodStoka) {
        LocalDate today = LocalDate.now();
        LocalDate expirationDate = foodStoka.getExpirationDate();
        
        if (expirationDate == null || expirationDate.isBefore(today)) {
            return 0.0;
        }
        
        long daysUntilExpiration = ChronoUnit.DAYS.between(today, expirationDate);
        return Math.min(100.0, (daysUntilExpiration / 30.0) * 100.0);
    }
    
    
    public FoodStoka extendExpirationDate(FoodStoka foodStoka, int days) {
        if (days <= 0) {
            throw new IllegalArgumentException("Дните за удължаване трябва да са положително число");
        }
        
        LocalDate currentExpirationDate = foodStoka.getExpirationDate();
        LocalDate newExpirationDate = currentExpirationDate.plusDays(days);
        
        foodStoka.setExpirationDate(newExpirationDate);
        return foodStoka;
    }
    
    public List<FoodStoka> prioritizeForSelling(List<FoodStoka> foodStokaList) {
        List<FoodStoka> sortedList = new ArrayList<>(foodStokaList);
        sortedList.sort(Comparator.comparing(FoodStoka::getExpirationDate));
        return sortedList;
    }
    
    
    public List<FoodStoka> applySeasonalDiscounts(List<FoodStoka> foodStokaList, 
                                                 List<String> seasonalItems, 
                                                 double discountPercent) {
        for (FoodStoka foodStoka : foodStokaList) {
            if (seasonalItems.contains(foodStoka.getName())) {
                BigDecimal currentPrice = foodStoka.getPrice();
                BigDecimal discountAmount = currentPrice.multiply(BigDecimal.valueOf(discountPercent / 100));
                BigDecimal newPrice = currentPrice.subtract(discountAmount);
                foodStoka.setPrice(newPrice);
            }
        }
        
        return foodStokaList;
    }
    public double calculateAverageShelfLife(List<FoodStoka> foodStokaList) {
        if (foodStokaList.isEmpty()) {
            return 0.0;
        }
        
        LocalDate today = LocalDate.now();
        double totalDays = 0.0;
        
        for (FoodStoka foodStoka : foodStokaList) {
            LocalDate expirationDate = foodStoka.getExpirationDate();
            if (expirationDate != null && !expirationDate.isBefore(today)) {
                long days = ChronoUnit.DAYS.between(today, expirationDate);
                totalDays += days;
            }
        }
        
        return totalDays / foodStokaList.size();
    }
}
