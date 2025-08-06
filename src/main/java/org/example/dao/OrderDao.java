package org.example.dao;

import org.example.model.Order;

import java.util.List;

public interface OrderDao {
    Order findById(Long id);
    List<Order> findByClientId(Long clientId);
    Order save(Order order);
    boolean cancelOrder(Long orderId);
}
