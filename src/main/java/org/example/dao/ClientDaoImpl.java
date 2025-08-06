package org.example.dao;

import org.example.model.Client;

import java.util.List;

public class ClientDaoImpl implements ClientDao {
    @Override
    public Client findById(Long id) {
        return null;
    }

    @Override
    public List<Client> findAll() {
        return List.of();
    }

    @Override
    public Client save(Client client) {
        return null;
    }

    @Override
    public void update(Client client) {

    }

    @Override
    public boolean delete(Long id) {
        return false;
    }
}
