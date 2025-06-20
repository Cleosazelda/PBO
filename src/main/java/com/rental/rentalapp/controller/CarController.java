package com.rental.rentalapp.controller;

import com.rental.rentalapp.model.Car;
import com.rental.rentalapp.model.enums.CarStatus;
import com.rental.rentalapp.service.CarService;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.Path;


import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/agent")
public class CarController {

    private final CarService carService;
    
    public CarController(CarService carService) {
        this.carService = carService;
    }

    @GetMapping("/cars")
    public String listCars(Model model, Authentication authentication) {
        // Cek authentication
        if (authentication == null || !authentication.getAuthorities().stream()
                .anyMatch(r -> r.getAuthority().equals("ROLE_AGENT"))) {
            return "redirect:/login";
        }
        
        model.addAttribute("cars", carService.getAllCars());
        model.addAttribute("agentUsername", authentication.getName());
        return "agent/cars";
    }

    @GetMapping("/cars/add")
    public String showAddCarForm(Model model, Authentication authentication) {
        if (authentication == null || !authentication.getAuthorities().stream()
                .anyMatch(r -> r.getAuthority().equals("ROLE_AGENT"))) {
            return "redirect:/login";
        }
        
        model.addAttribute("car", new Car());
        model.addAttribute("statuses", CarStatus.values());
        model.addAttribute("agentUsername", authentication.getName());
        model.addAttribute("isEdit", false);
        return "agent/car-form";
    }

    @GetMapping("/cars/edit/{id}")
    public String showEditCarForm(@PathVariable Long id, Model model, Authentication authentication) {
        if (authentication == null || !authentication.getAuthorities().stream()
                .anyMatch(r -> r.getAuthority().equals("ROLE_AGENT"))) {
            return "redirect:/login";
        }
        
        var carOpt = carService.getCarById(id);
        if (carOpt.isPresent()) {
            model.addAttribute("car", carOpt.get());
            model.addAttribute("statuses", CarStatus.values());
            model.addAttribute("agentUsername", authentication.getName());
            model.addAttribute("isEdit", true);
            return "agent/car-form";
        }
        return "redirect:/agent/cars";
    }

    @PostMapping("/cars/save")
public String saveCar(@ModelAttribute("car") Car car,
                      @RequestParam("imageFile") MultipartFile imageFile,
                      RedirectAttributes redirectAttributes,
                      Authentication authentication) {
    if (authentication == null || !authentication.getAuthorities().stream()
            .anyMatch(r -> r.getAuthority().equals("ROLE_AGENT"))) {
        return "redirect:/login";
    }

    try {
        // Jika ada file gambar yang di-upload
        if (!imageFile.isEmpty()) {
            // Simpan di direktori tertentu, misal: /uploads
            String filename = System.currentTimeMillis() + "_" + imageFile.getOriginalFilename();
            String uploadDir = new File("uploads/").getAbsolutePath();

            Path uploadPath = Paths.get(uploadDir);

            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            Path filePath = uploadPath.resolve(filename);
            Files.copy(imageFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // Simpan path gambar relative ke static folder
            car.setGambar("/uploads/" + filename);
        }

        carService.saveCar(car);
        redirectAttributes.addFlashAttribute("success", "Mobil berhasil disimpan!");
    } catch (Exception e) {
        redirectAttributes.addFlashAttribute("error", "Gagal menyimpan mobil: " + e.getMessage());
    }

    return "redirect:/agent/cars";
}

    @GetMapping("/cars/delete/{id}")
    public String deleteCar(@PathVariable Long id, RedirectAttributes redirectAttributes, Authentication authentication) {
        if (authentication == null || !authentication.getAuthorities().stream()
                .anyMatch(r -> r.getAuthority().equals("ROLE_AGENT"))) {
            return "redirect:/login";
        }
        
        try {
            carService.deleteCar(id);
            redirectAttributes.addFlashAttribute("success", "Mobil berhasil dihapus!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Gagal menghapus mobil: " + e.getMessage());
        }
        return "redirect:/agent/cars";
    }

    @GetMapping("/cars/filter")
    public String filterCars(@RequestParam(required = false) CarStatus status,
                             @RequestParam(required = false) String merek,
                             Model model,
                             Authentication authentication) {
        if (authentication == null || !authentication.getAuthorities().stream()
                .anyMatch(r -> r.getAuthority().equals("ROLE_AGENT"))) {
            return "redirect:/login";
        }

        model.addAttribute("cars", carService.filterCars(status, merek));
        model.addAttribute("statuses", CarStatus.values());
        model.addAttribute("agentUsername", authentication.getName());
        model.addAttribute("selectedStatus", status);
        model.addAttribute("selectedMerek", merek);
        return "agent/cars";
    }
}