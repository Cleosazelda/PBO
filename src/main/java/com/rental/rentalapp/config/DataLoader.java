package com.rental.rentalapp.config;

import com.rental.rentalapp.model.Agent;
import com.rental.rentalapp.repository.AgentRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class DataLoader implements CommandLineRunner {

    private final AgentRepository agentRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public DataLoader(AgentRepository agentRepository, BCryptPasswordEncoder passwordEncoder) {
        this.agentRepository = agentRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        // Cek apakah sudah ada agent
        if (agentRepository.count() == 0) {
            System.out.println("🔧 Creating default agents...");
            
            // Agent 1
            Agent agent1 = new Agent();
            agent1.setNamaDepan("Admin");
            agent1.setNamaBelakang("Agent");
            agent1.setUsername("admin");
            agent1.setEmail("admin@rental.com");
            agent1.setPassword(passwordEncoder.encode("admin123"));
            agentRepository.save(agent1);
            
            // Agent 2
            Agent agent2 = new Agent();
            agent2.setNamaDepan("Super");
            agent2.setNamaBelakang("Agent");
            agent2.setUsername("superagent");
            agent2.setEmail("agent@rental.com");
            agent2.setPassword(passwordEncoder.encode("agent123"));
            agentRepository.save(agent2);
            
            System.out.println("✅ Default agents created!");
            System.out.println("📋 Login credentials:");
            System.out.println("   Agent 1: admin@rental.com / admin123");
            System.out.println("   Agent 2: agent@rental.com / agent123");
        }
    }
}