package org.example.dao;

import org.example.model.Account;

import java.util.List;
import java.util.Optional;

public interface AccountDao {
    Optional<Account> findById(Long id);
    List<Account> findByClientId(Long clientId);
    Account save(Account account);
    void update(Account account);
    boolean delete(Long id); // аннулирование счёта
}
