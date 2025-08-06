package org.example.dao;

import org.example.exception.DaoException;
import org.example.model.Client;
import org.example.util.ConnectionManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ClientDaoImpl implements ClientDao {
    private static final String FIND_BY_ID_SQL = """
            SELECT client_id, first_name, last_name, birth_date, passport_number, phone_number, email, status
            FROM clients
            WHERE client_id = ?
            """;
    private static final String FIND_ALL_SQL = """
            SELECT client_id, first_name, last_name, birth_date, passport_number, phone_number, email, status
            FROM clients
            ORDER BY registration_date DESC
            """;
    private static final String SAVE_SQL = """
            INSERT INTO clients (first_name, last_name, birth_date, passport_number, phone_number, email, status)
            VALUES (?, ?, ?, ?, ?, ?, ?)
            RETURNING client_id
            """;
    private static final String UPDATE_SQL = """
            UPDATE clients
            SET first_name = ?, last_name = ?, birth_date = ?, passport_number = ?, phone_number = ?, email = ?, status = ?
            WHERE client_id = ?
            """;
    private static final String DELETE_SQL = """
            UPDATE clients
            SET status = 'DELETED'
            WHERE client_id = ?
            """;


    @Override
    public Optional<Client> findById(Long id) {
        try (var connection = ConnectionManager.get();
        var statement = connection.prepareStatement(FIND_BY_ID_SQL)) {
            statement.setLong(1, id);
            try(var result = statement.executeQuery()) {
                if (result.next()) {
                    return Optional.of(mapClient(result));
                }
            }
        } catch (SQLException throwable) {
            throw new DaoException("Не удалось найти клиента с id: " + id, throwable);
        }
        return Optional.empty();
    }

    @Override
    public List<Client> findAll() {
        List<Client> clients = new ArrayList<>();
        try(var connection = ConnectionManager.get();
        var statement = connection.prepareStatement(FIND_ALL_SQL)) {
            try (var result = statement.executeQuery()) {
                while (result.next()) {
                    clients.add(mapClient(result));
                }
            }
        } catch (SQLException throwable) {
            throw new DaoException("Не удалось найти клиентов", throwable);
        }
        return clients;
    }

    @Override
    public Client save(Client client) {
        try (var connection = ConnectionManager.get()) {
            connection.setAutoCommit(false);
            try (var statement = connection.prepareStatement(SAVE_SQL)) {
                setClientStatement(client, statement);
                try (var result = statement.executeQuery()) {
                    if (result.next()) {
                        connection.commit();
                        return new Client(
                                result.getLong("client_id"),
                                client.firstName(),
                                client.lastName(),
                                client.birthDate(),
                                client.passportNumber(),
                                client.phoneNumber(),
                                client.email(),
                                client.status()
                        );
                    }
                    connection.rollback();
                    throw new DaoException("Не удалось сохранить клиента: " + client);
                }
            } catch (SQLException throwable) {
                connection.rollback();
                throw new DaoException("Не удалось сохранить клиента: " + client, throwable);
            } finally {
                connection.setAutoCommit(true);
            }
        } catch (SQLException throwable) {
            throw new DaoException("Ошибка соединения при сохранении клиента: " + client, throwable);
        }
    }

    @Override
    public void update(Client client) {
        if (client.id() == null) throw new IllegalArgumentException("ID клиента не может быть null");
        try (var connection = ConnectionManager.get()) {
            connection.setAutoCommit(false);
            try (var statement = connection.prepareStatement(UPDATE_SQL)) {
                setClientStatement(client, statement);
                statement.setLong(8, client.id());
                int rowsAffected = statement.executeUpdate();
                if (rowsAffected == 0) {
                    connection.rollback();
                    throw new DaoException("Не удалось обновить клиента, клиент не найден: " + client.id());
                }
                connection.commit();
            } catch (SQLException throwable) {
                connection.rollback();
                throw new DaoException("Не удалось обновить клиента " + client, throwable);
            } finally {
                connection.setAutoCommit(true);
            }
        } catch (SQLException throwable) {
            throw new DaoException("Ошибка соединения при обновлении клиента: " + client, throwable);
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
                throw new DaoException("Не удалось удалить клиента с id: " + id, throwable);
            }
        } catch (SQLException throwable) {
            throw new DaoException("Ошибка соединения при удалении клиента: " + id, throwable);
        }
    }

    private void setClientStatement(Client client, PreparedStatement statement) throws SQLException {
        statement.setString(1, client.firstName());
        statement.setString(2, client.lastName());
        statement.setDate(3, (Date) client.birthDate());
        statement.setString(4, client.passportNumber());
        statement.setString(5, client.phoneNumber());
        statement.setString(6, client.email());
        statement.setString(7, client.status());
    }

    private Client mapClient(ResultSet resultSet) throws SQLException {
        return new Client(
                resultSet.getLong("client_id"),
                resultSet.getString("first_name"),
                resultSet.getString("last_name"),
                resultSet.getDate("birth_date"),
                resultSet.getString("passport_number"),
                resultSet.getString("phone_number"),
                resultSet.getString("email"),
                resultSet.getString("status")
        );
    }
}
