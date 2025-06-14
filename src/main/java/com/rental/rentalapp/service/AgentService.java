package com.rental.rentalapp.service;

import com.rental.rentalapp.model.Agent;
import com.rental.rentalapp.repository.AgentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AgentService {

    private final AgentRepository agentRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AgentService(AgentRepository agentRepository, PasswordEncoder passwordEncoder) {
        this.agentRepository = agentRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void registerAgent(Agent agent) {
        agent.setPassword(passwordEncoder.encode(agent.getPassword()));
        agentRepository.save(agent);
    }

    /**
     * PERBAIKAN: Mengembalikan Optional<Agent> agar sesuai dengan CustomUserDetailsService.
     */
    public Optional<Agent> findByEmail(String email) {
        return Optional.ofNullable(agentRepository.findByEmail(email));
    }

    public List<Agent> findAllAgents() {
        return agentRepository.findAll();
    }

    public Optional<Agent> findAgentById(Long id) {
        return agentRepository.findById(id);
    }

    public void saveAgent(Agent agent) {
        if (agent.getId() != null) {
            Agent existingAgent = agentRepository.findById(agent.getId()).orElse(null);
            if (agent.getPassword() == null || agent.getPassword().isEmpty()) {
                if (existingAgent != null) {
                    agent.setPassword(existingAgent.getPassword());
                }
            } else {
                agent.setPassword(passwordEncoder.encode(agent.getPassword()));
            }
        } else {
            agent.setPassword(passwordEncoder.encode(agent.getPassword()));
        }
        agentRepository.save(agent);
    }
    
    public void deleteAgentById(Long id) {
        agentRepository.deleteById(id);
    }

    public long countAllAgents() {
        return agentRepository.count();
    }
}