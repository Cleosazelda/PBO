package com.rental.rentalapp.security;

import com.rental.rentalapp.model.Agent;
import com.rental.rentalapp.model.Client;
import com.rental.rentalapp.service.AgentService;
import com.rental.rentalapp.service.ClientService;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;

@Service("customUserDetailsService")
public class CustomUserDetailsService implements UserDetailsService {

    private final ClientService clientService;
    private final AgentService agentService;

    public CustomUserDetailsService(ClientService clientService, AgentService agentService) {
        this.clientService = clientService;
        this.agentService = agentService;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        System.out.println("🔍 Attempting to load user by email: " + email);
        
        // Coba cari di Client terlebih dahulu
        Optional<Client> clientOpt = clientService.findByEmail(email);
        if (clientOpt.isPresent()) {
            Client client = clientOpt.get();
            System.out.println("✅ Found client: " + client.getEmail());
            System.out.println("🔍 Client password from DB: " + client.getPassword());
            
            return User.builder()
                    .username(client.getEmail())
                    .password(client.getPassword())
                    .authorities(Collections.singletonList(new SimpleGrantedAuthority("ROLE_CLIENT")))
                    .build();
        }

        // Jika tidak ditemukan di Client, coba cari di Agent
        Optional<Agent> agentOpt = agentService.findByEmail(email);
        if (agentOpt.isPresent()) {
            Agent agent = agentOpt.get();
            System.out.println("✅ Found agent: " + agent.getEmail());
            System.out.println("🔍 Agent password from DB: " + agent.getPassword());
            
            return User.builder()
                    .username(agent.getEmail())
                    .password(agent.getPassword())
                    .authorities(Collections.singletonList(new SimpleGrantedAuthority("ROLE_AGENT")))
                    .build();
        }

        System.out.println("❌ User not found with email: " + email);
        throw new UsernameNotFoundException("User not found with email: " + email);
    }
}