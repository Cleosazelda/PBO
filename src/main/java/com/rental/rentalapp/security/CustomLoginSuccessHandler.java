package com.rental.rentalapp.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@Component
public class CustomLoginSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, 
                                       HttpServletResponse response,
                                       Authentication authentication) throws IOException, ServletException {
        
        System.out.println("🔍 CustomLoginSuccessHandler triggered");
        System.out.println("Username: " + authentication.getName());
        System.out.println("Authorities: " + authentication.getAuthorities());
        
        // Set session attributes (opsional, karena Spring Security sudah handle)
        HttpSession session = request.getSession();
        
        // Cek role dan redirect sesuai
        boolean isAgent = authentication.getAuthorities().stream()
                .anyMatch(r -> r.getAuthority().equals("ROLE_AGENT"));
        boolean isClient = authentication.getAuthorities().stream()
                .anyMatch(r -> r.getAuthority().equals("ROLE_CLIENT"));
        
        String redirectUrl;
        
        if (isAgent) {
            System.out.println("✅ Agent login successful, redirecting to dashboard");
            session.setAttribute("agentUsername", authentication.getName());
            redirectUrl = "/agent/dashboard";
        } else if (isClient) {
            System.out.println("✅ Client login successful, redirecting to landing");
            session.setAttribute("clientUsername", authentication.getName());
            redirectUrl = "/client/landing";
        } else {
            System.out.println("❌ No valid role found, redirecting to login");
            redirectUrl = "/login?error=role";
        }
        
        System.out.println("🔄 Redirecting to: " + redirectUrl);
        response.sendRedirect(request.getContextPath() + redirectUrl);
    }
}