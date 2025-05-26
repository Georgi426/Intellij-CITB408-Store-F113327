package org.store;
import org.store.data.*;

import org.store.enums.StokaCategory;
import org.store.service.KasaService;
import org.store.service.ReceiptService;
import org.store.service.StoreService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.EnumMap;
import java.util.Map;

// Това е програма за управление на магазин, която симулира операциите на обикновен търговски обект
import java.nio.charset.StandardCharsets;

public class Main {
    public static void main(String[] args) {
        // Set console output encoding to UTF-8
        System.setProperty("file.encoding", "UTF-8");
        System.setProperty("console.encoding", "UTF-8");
        try {
            System.setOut(new java.io.PrintStream(System.out, true, StandardCharsets.UTF_8));
        } catch (Exception e) {
            System.err.println("Error setting up UTF-8 output: " + e.getMessage());
        }


            // Проверяваме дали днешната дата е правилно настроена

            System.out.println("============================================");
            System.out.println("Днешна дата: " + LocalDate.now());
            System.out.println("============================================");

            // Създаваме служител в магазина (касиер) на име Бен със заплата 2000

            Cashier cashier1 = new Cashier("Ben", new BigDecimal(2000));

            // Задаваме марж на печалба за различните категории продукти
            // Хранителните стоки имат 10% надценка, а нехранителните имат 15% надценка

            EnumMap<StokaCategory, Double> marginByCategory = new EnumMap<>(StokaCategory.class);
            marginByCategory.put(StokaCategory.FOOD, 10.0);
            marginByCategory.put(StokaCategory.NONFOOD, 15.0);
            // Създаваме магазин с: 15 дни преди изтичане на срока за отстъпка, 10% отстъпка за продукти близо до изтичане, и нашите дефинирани маржове
            Store store = new Store(15, 10.0, marginByCategory);

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

            // Създаваме два продукта: замразен грах (струва 5, продава се за 10) и тетрадка (струва 15, продава се за 20)
            Stoka stoka1 = new Stoka("001", "Замразен грах", new BigDecimal(5), new BigDecimal(10), expDate, StokaCategory.FOOD);
            Stoka stoka2 = new Stoka("002", "Тетрадка", new BigDecimal(15), new BigDecimal(20),expDate, StokaCategory.NONFOOD);

            // Проверяваме дали продуктите са с валиден срок на годност
            System.out.println("Проверка на срок на годност за " + stoka1.getName() + ": " +
                              (stoka1.isValid() ? "Валиден" : "Невалиден"));
            System.out.println("Проверка на срок на годност за " + stoka2.getName() + ": " +
                              (stoka2.isValid() ? "Валиден" : "Невалиден"));

            // Създаваме услуга за управление на операциите в магазина
            StoreService storeService = new StoreService(store);
            // Зареждаме инвентара на магазина с продукти (3 бройки замразен грах и 3 тетрадки)
            storeService.deliverStoka(stoka1);
            storeService.deliverStoka(stoka1);
            storeService.deliverStoka(stoka1);
            storeService.deliverStoka(stoka2);
            storeService.deliverStoka(stoka2);
            storeService.deliverStoka(stoka2);

            // Създаваме клиент с 1000 в портфейла
            Klient klient = new Klient(new BigDecimal(1000));
            // Клиентът добавя 1 пакет замразен грах и 2 тетрадки в кошницата си
            klient.addToCart(stoka1, 1);
            klient.addToCart(stoka2, 2);

            // Настройваме касов апарат с нашия касиер и го свързваме с нашия магазин
            Kasa kasa = new Kasa(cashier1, store);
            // Създаваме услуга за обработка на транзакции на тази каса
            KasaService kasaService = new KasaService(kasa, storeService);

            try {
                // Обработваме покупката на клиента и генерираме касова бележка
                Receipt receipt = kasaService.checkout(klient);
                // Създаваме услуга за работа с касови бележки
                ReceiptService receiptService = new ReceiptService(receipt);
                // Броим колко касови бележки са издадени
                int count = receiptService.countReceiptsIssued();
            
                // Правя черта отгоре
                System.out.println("\n===== КАСОВА БЕЛЕЖКА =====");
            
                // Печатам номера на бележката
                String receiptNumber = receipt.getSerialNumber();
                System.out.println("Номер на бележка: " + receiptNumber);
            
                // Печатам името на касиера
                String cashierName = kasaService.getCashierName();
                System.out.println("Касиер: " + cashierName);

                // Печатам днешната дата
                LocalDate receiptDate = LocalDate.now();
                System.out.println("Дата: " + receiptDate);

                // Черта между заглавието и артикулите
                System.out.println("----------------------");

                // Заглавие за артикулите
                System.out.println("Артикули:");

                // Събирам сумата на всички артикули
                BigDecimal totalSum = new BigDecimal("0.00");

                // Взимам артикулите от бележката
                Map<Stoka, Double> itemsInReceipt = receipt.getStoka();

                // Минавам през всички артикули и ги печатам
                for (Map.Entry<Stoka, Double> entry : itemsInReceipt.entrySet()) {
                    // Вземам артикула
                    Stoka item = entry.getKey();

                    // Вземам количеството
                    Double quantity = entry.getValue();

                    // Вземам цената
                    BigDecimal price = item.getPrice();

                    // Изчислявам общата сума за този артикул
                    BigDecimal quantityAsBigDecimal = new BigDecimal(quantity.toString());
                    BigDecimal itemTotal = price.multiply(quantityAsBigDecimal);

                    // Добавям към общата сума
                    totalSum = totalSum.add(itemTotal);

                    // Печатам артикула
                    System.out.println("  " + item.getName() + " - " + quantity +
                                       " бр. x " + price + " лв. = " +
                                       itemTotal + " лв.");
                }

                // Черта под артикулите
                System.out.println("----------------------");

                // Печатам общата сума
                System.out.println("Общо: " + totalSum + " лв.");

                // Черта отдолу
                System.out.println("==========================\n");

                // Изчисляваме и показваме общата сума
                BigDecimal total = receiptService.calculateTotalPrice();
                System.out.println("Общо платено: " + total + " лв.");
                
                // Намаляваме парите на клиента със стойността на покупката
                BigDecimal remainingMoney = klient.getMoney().subtract(total);
                System.out.println("Оставащи пари на клиента: " + remainingMoney + " лв.");
            }
            // Обработваме всякакви грешки, които могат да възникнат по време на процеса на покупка
            // (като недостатъчна наличност на продукти или клиентът няма достатъчно пари)
            catch (RuntimeException e) {
                // Показваме съобщението за грешка
                System.out.println("ГРЕШКА: " + e.getMessage());
            }
        }
}

