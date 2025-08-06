package org.example.dao;

import org.example.exception.DaoException;
import org.example.model.Account;
import org.example.util.ConnectionManager;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AccountDaoImpl implements AccountDao {
    private static final String FIND_BY_ID_SQL = """
            SELECT account_id, client_id, account_number, balance, currency, status
            FROM accounts
            WHERE account_id = ?
            """;
    private static final String FIND_BY_CLIENT_ID_SQL = """
            SELECT account_id, client_id, account_number, balance, currency, status
            FROM accounts
            WHERE client_id = ?
            ORDER BY opened_date DESC
            """;
    private static final String SAVE_SQL = """ 
            INSERT INTO accounts (client_id, account_number, balance, currency, status)
            VALUES (?, ?, ?, ?, ?)
            RETURNING account_id
            """;
    private static final String UPDATE_SQL = """
            UPDATE accounts
            SET client_id = ?, account_number = ?, balance = ?, currency = ?, status = ?
            WHERE account_id = ?
            """;
    private static final String DELETE_SQL = """
            UPDATE accounts
            SET status = 'CLOSED'
            WHERE account_id = ?
            """;    // только для одного Аккаунта

    // Аккаунт с определенным ID
    @Override
    public Optional<Account> findById(Long id) {    // Optional - контейнер для одного значения, который может содержать Task либо null
        try(var connection = ConnectionManager.get();
        var statement = connection.prepareStatement(FIND_BY_ID_SQL)) {
            statement.setLong(1, id);
            try(var result = statement.executeQuery()) {
                if (result.next()) {
                    return Optional.of(mapAccount(result));
                }
            }
        } catch (SQLException throwable) {
            throw new DaoException("Не удалось найти счёт с идентификатором " + id, throwable);
        }
        return Optional.empty();
    }
    // search for accounts
    @Override
    public List<Account> findByClientId(Long clientId) {
        List<Account> accounts = new ArrayList<>();
        try (var connection = ConnectionManager.get();
        var statement = connection.prepareStatement(FIND_BY_CLIENT_ID_SQL)) {
            statement.setLong(1, clientId);
            try (var result = statement.executeQuery()) {
                while (result.next()) {
                    accounts.add(mapAccount(result));
                }
            }
        } catch (SQLException throwable) {
            throw new DaoException("Не удалось найти счета по идентификатору клиента: " + clientId, throwable);
        }
        return accounts;
    }

    @Override
    public Account save(Account account) {
        try(var connection = ConnectionManager.get()) {
            connection.setAutoCommit(false);
            try(var statement = connection.prepareStatement(SAVE_SQL)) {
                statement.setLong(1, account.clientId());
                statement.setString(2, account.number());
                statement.setBigDecimal(3, account.balance());
                statement.setString(4, account.currency());
                statement.setString(5, account.status());
                try(var result = statement.executeQuery()) {
                    if (result.next()) {
                        connection.commit();
                        return new Account(result.getLong("account_id"),
                            account.clientId(),
                            account.number(),
                            account.balance(),
                            account.currency(),
                            account.status()
                        );
                    }
                    connection.rollback();
                    throw new DaoException("Не удалось сохранить счёт: " + account);
                }
            } catch (SQLException throwable) {
                connection.rollback();
                throw new DaoException("Не удалось сохранить счёт: " + account, throwable);
            } finally {
                connection.setAutoCommit(true);
            }
        } catch (SQLException throwable) {
            throw new DaoException("Ошибка соединения при сохранении счёта: " + account, throwable);
        }
    }

    @Override
    public void update(Account account) {
        if (account.id() == null) throw new IllegalArgumentException("ID счета не может быть null");
        try(var connection = ConnectionManager.get()) {
            connection.setAutoCommit(false);
            try (var statement = connection.prepareStatement(UPDATE_SQL)) {
                statement.setLong(1, account.clientId());
                statement.setString(2, account.number());
                statement.setBigDecimal(3, account.balance());
                statement.setString(4, account.currency());
                statement.setString(5, account.status());
                statement.setLong(6, account.id());
                int rowsAffected = statement.executeUpdate();
                if (rowsAffected == 0) {
                    connection.rollback();
                    throw new DaoException("Не удалось обновить счёт, счёт не найден: " + account.id());
                }
                connection.commit();
            } catch (SQLException throwable) {
                connection.rollback();
                throw new DaoException("Не удалось обновить счёт: " + account, throwable);
            } finally {
                connection.setAutoCommit(true);
            }
        } catch (SQLException throwable) {
            throw new DaoException("Ошибка соединения при обновлении счёта: " + account, throwable);
        }
    }

    @Override
    public boolean delete(Long id) {
        try (var connection = ConnectionManager.get()) {
            connection.setAutoCommit(false);
            try (var statement = connection.prepareStatement(DELETE_SQL)) {
                statement.setLong(1, id);
                int rowsAffected = statement.executeUpdate();
                if (rowsAffected > 0) {
                    connection.commit();
                    return true;
                }
                connection.rollback();
                return false;
            } catch (SQLException throwable) {
                connection.rollback();
                throw new DaoException("Не удалось аннулировать счёт с идентификатором: " + id, throwable);
            } finally {
                connection.setAutoCommit(true);
            }
        } catch (SQLException throwable) {
            throw new DaoException("Не удалось аннулировать счёт с идентификатором: " + id, throwable);
        }
    }


    private Account mapAccount(ResultSet resultSet) throws SQLException {
        return new Account(
            resultSet.getLong("account_id"),
            resultSet.getLong("client_id"),
            resultSet.getString("account_number"),
            resultSet.getBigDecimal("balance"),
            resultSet.getString("currency"),
            resultSet.getString("status")
        );
    }
}
