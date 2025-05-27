package org.store;

import org.store.data.*;
import org.store.enums.StokaCategory;
import org.store.service.KasaService;
import org.store.service.ReceiptService;
import org.store.service.StoreService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.nio.charset.StandardCharsets;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        System.setProperty("file.encoding", "UTF-8");
        System.setProperty("console.encoding", "UTF-8");
        try {
            System.setOut(new java.io.PrintStream(System.out, true, StandardCharsets.UTF_8));
        } catch (Exception e) {
            System.err.println("Error setting up UTF-8 output: " + e.getMessage());
        }

        Scanner scanner = new Scanner(System.in);

        System.out.println("============================================");
        System.out.println("Днешна дата: " + LocalDate.now());
        System.out.println("============================================");

        Cashier cashier1 = new Cashier("Ben", new BigDecimal(2000));

        EnumMap<StokaCategory, Double> marginByCategory = new EnumMap<>(StokaCategory.class);
        marginByCategory.put(StokaCategory.FOOD, 10.0);
        marginByCategory.put(StokaCategory.NONFOOD, 15.0);

        Store store = new Store(15, 10.0, marginByCategory);
        StoreService storeService = new StoreService(store);

        // Задаваме срок на годност 15 май 2024 г. за нашите продукти

        LocalDate expDate = LocalDate.of(2024, 5, 15);


        // Проверяваме дали срокът на годност е валиден (в бъдещето)

        LocalDate currentDate = LocalDate.now();
        if (expDate.isBefore(currentDate)) {
            System.out.println("Автоматично коригиране на срока...");
            // Коригираме срока на 1 година напред от днес

            expDate = currentDate.plusYears(1);
            System.out.println("Нов срок на годност: " + expDate);
        }
        System.out.println("============================================");
        List<Stoka> products = new ArrayList<>();
        products.add(new Stoka("001", "Замразен грах", new BigDecimal(5), new BigDecimal(10), expDate, StokaCategory.FOOD));
        products.add(new Stoka("002", "Тетрадка", new BigDecimal(15), new BigDecimal(20), expDate, StokaCategory.NONFOOD));
        products.add(new Stoka("003", "Кисело мляко", new BigDecimal(2), new BigDecimal(3), expDate, StokaCategory.FOOD));
        products.add(new Stoka("004", "Химикалка", new BigDecimal(1), new BigDecimal(2), expDate, StokaCategory.NONFOOD));
        products.add(new Stoka("005", "Бисквити", new BigDecimal(3), new BigDecimal(4), expDate, StokaCategory.FOOD));

        // Зареждаме склада с по 5 броя от всеки продукт (за да има наличности)

        for (Stoka product : products) {
            for (int i = 0; i < 5; i++) {
                storeService.deliverStoka(product);
            }
        }
        System.out.println("Доставени са по 5 броя от всеки продукт.");

        // Създаваме клиент с 100 лв.

        Klient klient = new Klient(new BigDecimal(1000));
        System.out.println("\nДобавяне на стоки в количката (въведете 0 за край):");
        while (true) {
            for (int i = 0; i < products.size(); i++) {
                System.out.println((i + 1) + ". " + products.get(i).getName());
            }
            System.out.print("Изберете продукт: ");
            int prodChoice = scanner.nextInt();
            if (prodChoice == 0) break;
            if (prodChoice < 1 || prodChoice > products.size()) continue;

            Stoka product = products.get(prodChoice - 1);
            System.out.print("Въведете количество: ");
            int qty = scanner.nextInt();
            klient.addToCart(product, qty);
        }

        Kasa kasa = new Kasa(cashier1, store);
        KasaService kasaService = new KasaService(kasa, storeService);

        try {
            Receipt receipt = kasaService.checkout(klient);
            ReceiptService receiptService = new ReceiptService(receipt);

            System.out.println("\n===== КАСОВА БЕЛЕЖКА =====");
            System.out.println("Номер на бележка: " + receipt.getSerialNumber());
            System.out.println("Касиер: " + kasaService.getCashierName());
            System.out.println("Дата: " + LocalDate.now());
            System.out.println("----------------------");
            System.out.println("Артикули:");

            BigDecimal totalSum = BigDecimal.ZERO;
            for (Map.Entry<Stoka, Double> entry : receipt.getStoka().entrySet()) {
                Stoka item = entry.getKey();
                Double qty = entry.getValue();
                BigDecimal price = item.getPrice();
                BigDecimal itemTotal = price.multiply(BigDecimal.valueOf(qty));
                totalSum = totalSum.add(itemTotal);
                System.out.println("  " + item.getName() + " - " + qty + " бр. x " + price + " лв. = " + itemTotal + " лв.");
            }

            System.out.println("----------------------");
            System.out.println("Общо: " + totalSum + " лв.");
            System.out.println("==========================");
            System.out.println("Общо платено: " + receiptService.calculateTotalPrice() + " лв.");
            System.out.println("Оставащи пари на клиента: " + klient.getMoney() + " лв.");

        } catch (RuntimeException e) {
            System.out.println("ГРЕШКА: " + e.getMessage());
        }
    }
}
