package com.translate.app.dao.repository;

import com.translate.app.dao.entity.Client;
import com.translate.app.dao.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClientRepository extends JpaRepository<Client, Integer> {

    Optional<Client> findByUser(User user);

    Optional<Client> findByEmail(String email);
}
