package com.rental.rentalapp.controller;

import com.rental.rentalapp.model.Client;
import com.rental.rentalapp.service.ClientService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Controller
public class LoginController {

    private final ClientService clientService;
    private final BCryptPasswordEncoder passwordEncoder;

    public LoginController(ClientService clientService, BCryptPasswordEncoder passwordEncoder) {
        this.clientService = clientService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/login")
    public String loginPage(@RequestParam(value = "error", required = false) String error, 
                           @RequestParam(value = "logout", required = false) String logout,
                           @RequestParam(value = "register_success", required = false) String registerSuccess,
                           Model model) {
        System.out.println("🔍 Login page accessed");
        System.out.println("Error param: " + error);
        System.out.println("Logout param: " + logout);
        System.out.println("Register success param: " + registerSuccess);
        
        if (error != null) {
            if ("role".equals(error)) {
                model.addAttribute("error", "Role tidak valid.");
            } else if ("access_denied".equals(error)) {
                model.addAttribute("error", "Akses ditolak.");
            } else {
                model.addAttribute("error", "Email atau password salah.");
            }
        }
        if (logout != null) {
            model.addAttribute("message", "Anda telah berhasil logout.");
        }
        if (registerSuccess != null) {
            model.addAttribute("success", "Registrasi berhasil! Silakan login.");
        }
        
        return "public/login";
    }

    @GetMapping("/register")
    public String showRegisterForm() {
        return "public/register";
    }

    @PostMapping("/register")
    public String processRegister(@RequestParam String nik,
                                  @RequestParam String sim,
                                  @RequestParam String email,
                                  @RequestParam String namaDepan,
                                  @RequestParam String namaBelakang,
                                  @RequestParam String alamat,
                                  @RequestParam String telepon,
                                  @RequestParam String password,
                                  @RequestParam String tanggalLahir,
                                  Model model) {

        System.out.println("🔍 Register attempt for email: " + email);
        
        if (clientService.findByEmail(email).isPresent()) {
            System.out.println("❌ Email already exists: " + email);
            model.addAttribute("error", "Email sudah terdaftar");
            return "public/register";
        }

        Client client = new Client();
        client.setNik(nik);
        client.setSim(sim);
        client.setEmail(email);
        client.setNamaDepan(namaDepan);
        client.setNamaBelakang(namaBelakang);
        client.setAlamat(alamat);
        client.setTelepon(telepon);
        
        String encodedPassword = passwordEncoder.encode(password);
        System.out.println("🔍 Original password length: " + password.length());
        System.out.println("🔍 Encoded password: " + encodedPassword);
        client.setPassword(encodedPassword);
        
        client.setTanggalLahir(LocalDate.parse(tanggalLahir));
        client.setTanggalPendaftaran(LocalDate.now());

        clientService.saveClient(client);
        System.out.println("✅ Client registered successfully for email: " + client.getEmail());
        
        return "redirect:/login?register_success";
    }

    // Hapus method defaultAfterLogin karena sudah ditangani oleh CustomLoginSuccessHandler
}