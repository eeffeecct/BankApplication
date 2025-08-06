package org.example;

import org.example.dao.AccountDao;
import org.example.dao.AccountDaoImpl;
import org.example.model.Account;
import org.example.util.ConnectionManager;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Optional;
import java.util.List;

public class Main {
    public static void main(String[] args) throws SQLException {
        AccountDao accountDao = new AccountDaoImpl();

        // Тест 1: findById (существующий счет)
        System.out.println("=== Тест 1: Поиск счета по ID ===");
        try {
            Optional<Account> account = accountDao.findById(4L); // Используй 4L, если не сбросил последовательность
            if (account.isPresent()) {
                System.out.println("Найден счет: " + account.get());
            } else {
                System.out.println("Счет с id=4 не найден");
            }
        } catch (Exception e) {
            System.err.println("Ошибка при поиске счета: " + e.getMessage());
        }

        // Тест 2: findById (несуществующий счет)
        System.out.println("\n=== Тест 2: Поиск несуществующего счета ===");
        try {
            Optional<Account> account = accountDao.findById(999L);
            if (account.isPresent()) {
                System.out.println("Найден счет: " + account.get());
            } else {
                System.out.println("Счет с id=999 не найден");
            }
        } catch (Exception e) {
            System.err.println("Ошибка при поиске счета: " + e.getMessage());
        }

        // Тест 3: findByClientId
        System.out.println("\n=== Тест 3: Поиск счетов клиента ===");
        try {
            List<Account> accounts = accountDao.findByClientId(100L);
            if (!accounts.isEmpty()) {
                System.out.println("Счета клиента 100: " + accounts);
            } else {
                System.out.println("У клиента 100 нет счетов");
            }
        } catch (Exception e) {
            System.err.println("Ошибка при поиске счетов клиента: " + e.getMessage());
        }

        // Тест 4: save
        System.out.println("\n=== Тест 4: Создание нового счета ===");
        Account newAccount = new Account(null, 300L, "5556667778", new BigDecimal("2000.00"), "RUB", "ACTIVE");
        try {
            Account saved = accountDao.save(newAccount);
            System.out.println("Создан счет: " + saved);
        } catch (Exception e) {
            System.err.println("Ошибка при создании счета: " + e.getMessage());
        }

        // Тест 5: save (дублирующий account_number)
        System.out.println("\n=== Тест 5: Создание счета с дублирующим номером ===");
        Account duplicate = new Account(null, 300L, "5556667778", new BigDecimal("3000.00"), "RUB", "ACTIVE");
        try {
            accountDao.save(duplicate);
            System.out.println("Успех (не должно быть)");
        } catch (Exception e) {
            System.err.println("Ожидаемая ошибка при создании дубликата: " + e.getMessage());
        }

        // Тест 6: update
        System.out.println("\n=== Тест 6: Обновление счета ===");
        Account updated = new Account(4L, 100L, "1234567890", new BigDecimal("6000.00"), "RUB", "ACTIVE"); // Используй 4L, если не сбросил
        try {
            accountDao.update(updated);
            System.out.println("Счет обновлен");
            Optional<Account> found = accountDao.findById(4L); // Используй 4L, если не сбросил
            System.out.println("Проверка: " + found.orElse(null));
        } catch (Exception e) {
            System.err.println("Ошибка при обновлении счета: " + e.getMessage());
        }

        // Тест 7: update (несуществующий счет)
        System.out.println("\n=== Тест 7: Обновление несуществующего счета ===");
        Account notFound = new Account(999L, 100L, "1234567890", new BigDecimal("6000.00"), "RUB", "ACTIVE");
        try {
            accountDao.update(notFound);
            System.out.println("Успех (не должно быть)");
        } catch (Exception e) {
            System.err.println("Ожидаемая ошибка при обновлении: " + e.getMessage());
        }

        // Тест 8: update (null id)
        System.out.println("\n=== Тест 8: Обновление с null id ===");
        Account nullId = new Account(null, 100L, "1234567890", new BigDecimal("6000.00"), "RUB", "ACTIVE");
        try {
            accountDao.update(nullId);
            System.out.println("Успех (не должно быть)");
        } catch (Exception e) {
            System.err.println("Ожидаемая ошибка при null id: " + e.getMessage());
        }

        // Тест 9: delete
        System.out.println("\n=== Тест 9: Аннулирование счета ===");
        try {
            boolean deleted = accountDao.delete(4L); // Используй 4L, если не сбросил
            System.out.println("Счет аннулирован: " + deleted);
            Optional<Account> found = accountDao.findById(4L); // Используй 4L, если не сбросил
            System.out.println("Проверка статуса: " + (found.isPresent() ? found.get().status() : "не найден"));
        } catch (Exception e) {
            System.err.println("Ошибка при аннулировании счета: " + e.getMessage());
        }

        // Тест 10: delete (несуществующий счет)
        System.out.println("\n=== Тест 10: Аннулирование несуществующего счета ===");
        try {
            boolean deleted = accountDao.delete(999L);
            System.out.println("Счет аннулирован: " + deleted);
        } catch (Exception e) {
            System.err.println("Ошибка при аннулировании: " + e.getMessage());
        }
    }
}