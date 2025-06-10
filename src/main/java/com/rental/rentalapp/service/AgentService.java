package com.rental.rentalapp.service;

import com.rental.rentalapp.model.Agent;
import com.rental.rentalapp.repository.AgentRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AgentService {
    private final AgentRepository agentRepository;

    public AgentService(AgentRepository agentRepository) {
        this.agentRepository = agentRepository;
    }

    public Optional<Agent> findByUsername(String username) {
        try {
            return Optional.ofNullable(agentRepository.findByUsername(username));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public Optional<Agent> findByEmail(String email) {
        try {
            return Optional.ofNullable(agentRepository.findByEmail(email));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public Agent saveAgent(Agent agent) {
        return agentRepository.save(agent);
    }

    public long countAllAgents() {
        try {
            return agentRepository.count();
        } catch (Exception e) {
            return 0L;
        }
    }
}
