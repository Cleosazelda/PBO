package com.rental.rentalapp.config;

import com.rental.rentalapp.model.Agent;
import com.rental.rentalapp.service.AgentService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AgentSeeder implements CommandLineRunner {

    private final AgentService agentService;
    private final BCryptPasswordEncoder passwordEncoder;

    public AgentSeeder(AgentService agentService, BCryptPasswordEncoder passwordEncoder) {
        this.agentService = agentService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        // Cek apakah sudah ada agent
        if (agentService.findByEmail("admin@rental.com").isEmpty()) {
            Agent agent = new Agent();
            agent.setNamaDepan("Admin");
            agent.setNamaBelakang("Rental");
            agent.setEmail("admin@rental.com");
            agent.setUsername("admin");
            agent.setPassword(passwordEncoder.encode("admin123"));
            
            agentService.saveAgent(agent);
            System.out.println("✅ Default agent created: admin@rental.com / admin123");
        }
    }
}