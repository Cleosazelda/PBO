package com.rental.rentalapp.controller;

import com.rental.rentalapp.model.Rental;
import com.rental.rentalapp.model.Client;
import com.rental.rentalapp.model.Car;
import com.rental.rentalapp.model.enums.PaymentMethod;
import com.rental.rentalapp.model.enums.RentalStatus;
import com.rental.rentalapp.service.RentalService;
import com.rental.rentalapp.service.ClientService;
import com.rental.rentalapp.service.CarService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/agent")
public class RentalController {

    private final RentalService rentalService;
    private final ClientService clientService;
    private final CarService carService;

    public RentalController(RentalService rentalService, ClientService clientService, CarService carService) {
        this.rentalService = rentalService;
        this.clientService = clientService;
        this.carService = carService;
    }

    @GetMapping("/rentals")
    public String listRentals(Model model, Authentication authentication) {
        if (authentication == null || !authentication.getAuthorities().stream()
                .anyMatch(r -> r.getAuthority().equals("ROLE_AGENT"))) {
            return "redirect:/login";
        }
        
        List<Rental> rentals = rentalService.getAllRentals();
        model.addAttribute("rentals", rentals);
        model.addAttribute("agentUsername", authentication.getName());
        return "agent/rentals";
    }

    @GetMapping("/rentals/add")
    public String showAddRentalForm(Model model, Authentication authentication) {
        if (authentication == null || !authentication.getAuthorities().stream()
                .anyMatch(r -> r.getAuthority().equals("ROLE_AGENT"))) {
            return "redirect:/login";
        }
        
        model.addAttribute("rental", new Rental());
        model.addAttribute("clients", clientService.getAllClients());
        model.addAttribute("cars", carService.getAvailableCars()); // Pastikan ini mengembalikan mobil yang TERSEDIA
        model.addAttribute("paymentMethods", PaymentMethod.values());
        model.addAttribute("rentalStatuses", RentalStatus.values());
        model.addAttribute("agentUsername", authentication.getName());
        model.addAttribute("isEdit", false);
        return "agent/rental-form";
    }

    @GetMapping("/rentals/edit/{id}")
    public String showEditRentalForm(@PathVariable Long id, Model model, Authentication authentication) {
        if (authentication == null || !authentication.getAuthorities().stream()
                .anyMatch(r -> r.getAuthority().equals("ROLE_AGENT"))) {
            return "redirect:/login";
        }
        
        var rentalOpt = rentalService.findById(id);
        if (rentalOpt.isPresent()) {
            model.addAttribute("rental", rentalOpt.get());
            model.addAttribute("clients", clientService.getAllClients());
            // Saat edit, semua mobil bisa ditampilkan, karena mobil yang sedang disewa mungkin sedang diedit.
            model.addAttribute("cars", carService.getAllCars()); 
            model.addAttribute("paymentMethods", PaymentMethod.values());
            model.addAttribute("rentalStatuses", RentalStatus.values());
            model.addAttribute("agentUsername", authentication.getName());
            model.addAttribute("isEdit", true);
            return "agent/rental-form";
        }
        return "redirect:/agent/rentals";
    }

    @PostMapping("/rentals/save")
    public String saveRental(@ModelAttribute("rental") Rental rental, RedirectAttributes redirectAttributes, Authentication authentication) {
        if (authentication == null || !authentication.getAuthorities().stream()
                .anyMatch(r -> r.getAuthority().equals("ROLE_AGENT"))) {
            return "redirect:/login";
        }
        
        try {
            rentalService.saveRental(rental);
            redirectAttributes.addFlashAttribute("success", "Rental berhasil disimpan!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Gagal menyimpan rental: " + e.getMessage());
        }
        return "redirect:/agent/rentals";
    }

    @GetMapping("/rentals/delete/{id}")
    public String deleteRental(@PathVariable Long id, RedirectAttributes redirectAttributes, Authentication authentication) {
        if (authentication == null || !authentication.getAuthorities().stream()
                .anyMatch(r -> r.getAuthority().equals("ROLE_AGENT"))) {
            return "redirect:/login";
        }
        
        try {
            rentalService.deleteRental(id);
            redirectAttributes.addFlashAttribute("success", "Rental berhasil dihapus!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Gagal menghapus rental: " + e.getMessage());
        }
        return "redirect:/agent/rentals";
    }

    @GetMapping("/rentals/view/{id}")
    public String viewRental(@PathVariable Long id, Model model, Authentication authentication) {
        if (authentication == null || !authentication.getAuthorities().stream()
                .anyMatch(r -> r.getAuthority().equals("ROLE_AGENT"))) {
            return "redirect:/login";
        }
        
        var rentalOpt = rentalService.findById(id);
        if (rentalOpt.isPresent()) {
            model.addAttribute("rental", rentalOpt.get());
            model.addAttribute("agentUsername", authentication.getName());
            return "agent/rental-detail";
        }
        return "redirect:/agent/rentals";
    }
}