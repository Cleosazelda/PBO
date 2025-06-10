package com.rental.rentalapp.controller;

import com.rental.rentalapp.service.CarService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/public")
public class PublicController {
    
    private final CarService carService;
    
    public PublicController(CarService carService) {
        this.carService = carService;
    }
    
    // Public access to car list (no login required)
    @GetMapping("/cars")
    public String publicCarList(Model model) {
        model.addAttribute("cars", carService.getAllCars());
        return "public/car-list"; // Buat template baru untuk public
    }
    
    // Public access to car details (no login required)
    @GetMapping("/cars/{id}")
    public String publicCarDetails(@PathVariable Long id, Model model, HttpSession session) {
        var carOpt = carService.getCarById(id);
        if (carOpt.isEmpty()) {
            return "redirect:/public/cars";
        }
        
        model.addAttribute("car", carOpt.get());
        
        // Check if user is logged in to show different buttons
        String clientNik = (String) session.getAttribute("clientNik");
        model.addAttribute("isLoggedIn", clientNik != null);
        
        return "public/car-details"; // Buat template baru untuk public
    }
}