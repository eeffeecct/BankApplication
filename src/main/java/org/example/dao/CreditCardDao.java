package org.example.dao;

import org.example.model.CreditCard;

import java.util.List;

public interface CreditCardDao {
    CreditCard findById(Long id);
    List<CreditCard> findByClientId(Long clientId);
    CreditCard save(CreditCard card);
    void update(CreditCard card);
    boolean blockCard(Long cardId); // блокировка карты
}
