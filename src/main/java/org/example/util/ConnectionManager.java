package org.example.util;

// Чтение настроек из application.properties. - DONE
// Загрузка драйвера PostgreSQL. - DONE
// Создание пула соединений (BlockingQueue). - DONE
// Возвращает соединения через get(). - DONE
// Управление транзакциями.
// Возвращает соединение в пул при close() - DONE

import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public final class ConnectionManager {
    private static final String PASSWORD_KEY = "db.password";
    private static final String USERNAME_KEY = "db.username";
    private static final String URL_KEY = "db.url";
    private static BlockingQueue<Connection> pool;
    private static final String POOL_SIZE_KEY = "db.pool.size";
    private static final Integer DEFAULT_POOL_SIZE = 10;
    private static List<Connection> sourceConnections;

    static {
        initConnectionPool();
    }

    private ConnectionManager() {}

    // создание пула
    private static void initConnectionPool() {
        var poolSize = PropertiesUtil.get(POOL_SIZE_KEY);
        var size = poolSize == null ? DEFAULT_POOL_SIZE : Integer.parseInt(poolSize);
        pool = new ArrayBlockingQueue<>(size); // инициализация соединения с размером 10
        sourceConnections = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            var connection = open();
            var proxyConnection = Proxy.newProxyInstance(ConnectionManager.class.
                            getClassLoader(),
                            new Class[]{Connection.class},
                            (proxy, method, args)
                            -> method.getName().equals("close")
                            ? pool.add((Connection) proxy)
                            : method.invoke(connection, args));
            pool.add((Connection) proxyConnection); // заполнение
            sourceConnections.add(connection);
            // для каждого соединения создаётся прокси-объект (чтобы перехватить вызов close() и возвращать соединение в пул
        }
    }

    // открытие соединения к БД
    public static Connection open() {
        try {
            return DriverManager.getConnection(
                    PropertiesUtil.get(URL_KEY),
                    PropertiesUtil.get(USERNAME_KEY),
                    PropertiesUtil.get(PASSWORD_KEY)
            );
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при подключении к бд", e);
        }
    }

    public static Connection get() {
        try {
            return pool.take();
        } catch (InterruptedException e) {
            throw new RuntimeException("Не удалось получить соединение из пула", e);
        }
    }
}
