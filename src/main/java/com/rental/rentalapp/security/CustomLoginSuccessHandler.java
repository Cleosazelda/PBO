package com.rental.rentalapp.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession; // Pastikan ini diimpor
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
        
        HttpSession session = request.getSession();
        String redirectUrl = null;

        // Coba ambil URL redirect yang disimpan di sesi
        String savedRequestUrl = (String) session.getAttribute("redirectAfterLogin");
        if (savedRequestUrl != null && !savedRequestUrl.isEmpty()) {
            redirectUrl = savedRequestUrl;
            session.removeAttribute("redirectAfterLogin"); // Hapus atribut setelah digunakan
            System.out.println("🔄 Found saved redirect URL: " + redirectUrl);
        } else {
            // Jika tidak ada URL tersimpan, tentukan redirect berdasarkan role
            boolean isAgent = authentication.getAuthorities().stream()
                    .anyMatch(r -> r.getAuthority().equals("ROLE_AGENT"));
            boolean isClient = authentication.getAuthorities().stream()
                    .anyMatch(r -> r.getAuthority().equals("ROLE_CLIENT"));
            
            if (isAgent) {
                System.out.println("✅ Agent login successful, redirecting to agent dashboard");
                redirectUrl = "/agent/dashboard";
            } else if (isClient) {
                System.out.println("✅ Client login successful, redirecting to client landing");
                redirectUrl = "/client/landing";
            } else {
                System.out.println("❌ No valid role found, redirecting to login");
                redirectUrl = "/login?error=role";
            }
        }
        
        System.out.println("🔄 Final redirecting to: " + redirectUrl);
        response.sendRedirect(request.getContextPath() + redirectUrl);
    }
}