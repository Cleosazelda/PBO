package com.rental.rentalapp.controller;

import com.rental.rentalapp.service.AgentService;
import com.rental.rentalapp.service.CarService;
import com.rental.rentalapp.service.ClientService;
import com.rental.rentalapp.service.RentalService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/agent")
public class AgentDashboardController {
    
    private final AgentService agentService;
    private final CarService carService;
    private final ClientService clientService;
    private final RentalService rentalService;

    public AgentDashboardController(AgentService agentService, 
                                   CarService carService,
                                   ClientService clientService, 
                                   RentalService rentalService) {
        this.agentService = agentService;
        this.carService = carService;
        this.clientService = clientService;
        this.rentalService = rentalService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model, Authentication authentication) {
        System.out.println("🔍 Agent dashboard accessed");
        
        // Cek apakah user sudah login dan memiliki role AGENT
        if (authentication == null || !authentication.isAuthenticated()) {
            System.out.println("❌ Authentication is null or not authenticated");
            return "redirect:/login";
        }
        
        // Cek apakah user memiliki role AGENT
        boolean hasAgentRole = authentication.getAuthorities().stream()
                .anyMatch(r -> r.getAuthority().equals("ROLE_AGENT"));
        
        if (!hasAgentRole) {
            System.out.println("❌ User doesn't have AGENT role");
            return "redirect:/login?error=access_denied";
        }
        
        String agentUsername = authentication.getName();
        System.out.println("✅ Agent authenticated: " + agentUsername);

        // Ambil data statistik dari masing-masing service
        long totalClients = clientService.countAllClients();
        long totalAgents = agentService.countAllAgents();
        long totalRentals = rentalService.countAllRentals();
        long totalCars = carService.countAllCars();

        // Tambahkan data ke model untuk template
        model.addAttribute("agentUsername", agentUsername);
        model.addAttribute("totalClients", totalClients);
        model.addAttribute("totalAgents", totalAgents);
        model.addAttribute("totalRentals", totalRentals);
        model.addAttribute("totalCars", totalCars);

        System.out.println("✅ Agent dashboard data loaded successfully");
        return "agent/dashboard";
    }
}