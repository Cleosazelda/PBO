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

    public UserPrincipal(Agent agent) {
        this.username = agent.getEmail();
        this.password = agent.getPassword();
        this.role = "ROLE_AGENT";
    }

    public UserPrincipal(Client client) {
        this.username = client.getEmail();
        this.password = client.getPassword();
        this.role = "ROLE_CLIENT";
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
