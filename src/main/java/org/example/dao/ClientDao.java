package org.example.dao;

import org.example.model.Client;

import java.util.List;

public interface ClientDao {
    Client findById(Long id);
    List<Client> findAll();
    Client save(Client client);
    void update(Client client);
    boolean delete(Long id);
}
