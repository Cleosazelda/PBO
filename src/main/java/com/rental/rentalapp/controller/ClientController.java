package com.rental.rentalapp.controller;

import com.rental.rentalapp.model.Client;
import com.rental.rentalapp.model.Car;
import com.rental.rentalapp.service.ClientService;
import com.rental.rentalapp.service.RentalService;
import com.rental.rentalapp.service.CarService;
import com.rental.rentalapp.security.UserPrincipal;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/client")
public class ClientController {
    
    private final ClientService clientService;
    private final RentalService rentalService;
    private final CarService carService;
    
    public ClientController(ClientService clientService, RentalService rentalService, CarService carService) {
        this.clientService = clientService;
        this.rentalService = rentalService;
        this.carService = carService;
    }
    
    // Helper method untuk mendapatkan client saat ini berdasarkan UserPrincipal
    private Client getCurrentClient(UserPrincipal user) {
        return clientService.findByEmail(user.getUsername())
                .orElseThrow(() -> new RuntimeException("Client not found for user: " + user.getUsername()));
    }
    
    // Helper method untuk mendapatkan client berdasarkan session NIK
    private Client getCurrentClientBySession(HttpSession session) {
        String clientNik = (String) session.getAttribute("clientNik");
        if (clientNik == null) {
            throw new RuntimeException("Client not logged in");
        }
        return clientService.findByNik(clientNik)
                .orElseThrow(() -> new RuntimeException("Client not found for NIK: " + clientNik));
    }
    
    @GetMapping("/profile")
    public String profile(@AuthenticationPrincipal UserPrincipal user, HttpSession session, Model model) {
        Client client;
        
        // Try to get client from UserPrincipal first, then from session
        if (user != null) {
            client = getCurrentClient(user);
        } else {
            client = getCurrentClientBySession(session);
        }
        
        model.addAttribute("client", client);
        return "client/profile";
    }
    
    @GetMapping("/rentals")
    public String rentalHistory(@AuthenticationPrincipal UserPrincipal user, HttpSession session, Model model) {
        Client client;
        
        // Try to get client from UserPrincipal first, then from session
        if (user != null) {
            client = getCurrentClient(user);
        } else {
            client = getCurrentClientBySession(session);
        }
        
        model.addAttribute("rentalList", rentalService.findByClient(client));
        return "client/rental";
    }
    
    @GetMapping("/landing")
    public String landing(@AuthenticationPrincipal UserPrincipal user, HttpSession session, Model model) {
        Client client;
        
        // Try to get client from UserPrincipal first, then from session
        if (user != null) {
            client = getCurrentClient(user);
        } else {
            try {
                client = getCurrentClientBySession(session);
                model.addAttribute("client", client);
            } catch (RuntimeException e) {
                // Client not logged in, continue without client data
            }
        }
        
        return "client/landing";
    }
    
    // Modified: Allow public access to car list (no login required)
    @GetMapping("/cars")
    public String carList(HttpSession session, Model model) {
        // Add cars to model regardless of login status
        model.addAttribute("cars", carService.getAllCars());
        
        // Check if client is logged in and add this info to model
        String clientNik = (String) session.getAttribute("clientNik");
        model.addAttribute("isLoggedIn", clientNik != null);
        
        return "client/car-list";
    }
    
    // Modified: Allow public access to car details, but require login for rental
    @GetMapping("/cars/{id}")
    public String carDetails(@PathVariable Long id, HttpSession session, Model model) {
        var carOpt = carService.getCarById(id);
        if (carOpt.isEmpty()) {
            return "redirect:/client/cars";
        }
        
        model.addAttribute("car", carOpt.get());
        
        // Check if client is logged in
        String clientNik = (String) session.getAttribute("clientNik");
        model.addAttribute("isLoggedIn", clientNik != null);
        
        return "client/car-details";
    }
    
    // Keep login requirement for rental form
    @GetMapping("/rental/{carId}")
    public String rentalForm(@PathVariable Long carId, HttpSession session, Model model) {
        String clientNik = (String) session.getAttribute("clientNik");
        if (clientNik == null) {
            session.setAttribute("redirectAfterLogin", "/client/rental/" + carId);
            return "redirect:/login";
        }
        
        var carOpt = carService.getCarById(carId);
        if (carOpt.isEmpty()) {
            return "redirect:/client/cars";
        }
        
        model.addAttribute("car", carOpt.get());
        return "client/rental-form";
    }
}