package org.example.dao;

import org.example.model.Payment;

import java.util.List;

public interface PaymentDao {
    Payment findById(Long id);
    List<Payment> findAllByAccountId(Long accountId);
    boolean makePayment(Payment payment); // перевод денег
    boolean payOrder(Long orderId, Long cardId); // оплата заказа
}
