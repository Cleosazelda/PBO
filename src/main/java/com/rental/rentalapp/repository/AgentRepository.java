package com.rental.rentalapp.repository;

import com.rental.rentalapp.model.Agent;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface AgentRepository extends JpaRepository<Agent, Long> {
    Agent findByUsername(String username);
    Agent findByEmail(String email);
}