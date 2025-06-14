package com.rental.rentalapp.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import com.rental.rentalapp.model.Agent;
import com.rental.rentalapp.model.Client;

import java.util.Collection;
import java.util.Collections;

public class UserPrincipal implements UserDetails {

    private String username;
    private String password;
    private String role;
    private Client client; // <-- ADD THIS FIELD
    private Agent agent;

    public UserPrincipal(Agent agent) {
        this.username = agent.getEmail();
        this.password = agent.getPassword();
        this.role = "ROLE_AGENT";
        this.agent = agent; // Store the agent object
        this.client = null;
    }

    public UserPrincipal(Client client) {
        this.username = client.getEmail();
        this.password = client.getPassword();
        this.role = "ROLE_CLIENT";
        this.client = client; // <-- STORE THE CLIENT OBJECT HERE
        this.agent = null; 
    }

    // Getter to retrieve the Client object
    public Client getClient() { // <-- ADD THIS GETTER METHOD
        return client;
    }

     // Getter to retrieve the Agent object (if needed, e.g., in an AgentController)
    public Agent getAgent() {
        return agent;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority(role));
    }

    @Override public String getPassword() { return password; }
    @Override public String getUsername() { return username; }
    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }
}
