// src/main/java/com/rental/rentalapp/controller/ClientController.java
package com.rental.rentalapp.controller;

import com.rental.rentalapp.model.Rental;
import com.rental.rentalapp.model.Client;
import com.rental.rentalapp.model.Car;
import com.rental.rentalapp.model.enums.CarStatus;
import com.rental.rentalapp.model.enums.RentalStatus;
import com.rental.rentalapp.model.enums.PaymentMethod;

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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.format.annotation.DateTimeFormat;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/client") // <--- Pastikan ini ada dan benar
public class ClientController {

    private final ClientService clientService;
    private final RentalService rentalService;
    private final CarService carService;

    public ClientController(ClientService clientService, RentalService rentalService, CarService carService) {
        this.clientService = clientService;
        this.rentalService = rentalService;
        this.carService = carService;
    }

    private Client getCurrentClient(UserPrincipal userPrincipal) {
        if (userPrincipal == null) {
            throw new RuntimeException("User not authenticated.");
        }
        Client client = userPrincipal.getClient();
        if (client == null) {
            // Ini akan terjadi jika yang login adalah Agent, atau UserPrincipal tidak terisi Client.
            // Dalam kasus Anda, ini hanya akan terjadi jika yang login adalah AGENT
            // dan mencoba akses path CLIENT (yang seharusnya sudah dicegah oleh .hasRole("CLIENT"))
            throw new RuntimeException("Client object not found in UserPrincipal. This might indicate an unauthorized access attempt or misconfiguration.");
        }
        return client;
    }

    // Method getCurrentClientBySession (jika masih ada) bisa dipertahankan jika ada alur login non-Spring Security
    // Tetapi jika hanya mengandalkan Spring Security (UserPrincipal), bisa dihapus.
    // Untuk saat ini, asumsikan itu hanya fallback jika userPrincipal null.

    @GetMapping("/profile")
    public String profile(@AuthenticationPrincipal UserPrincipal userPrincipal, Model model) {
        Client client = getCurrentClient(userPrincipal);
        model.addAttribute("client", client);
        model.addAttribute("title", "Profil Saya");
        return "client/profile";
    }

    @GetMapping("/rentals")
    public String rentalHistory(@AuthenticationPrincipal UserPrincipal userPrincipal, Model model) {
        Client client = getCurrentClient(userPrincipal);
        model.addAttribute("rentalList", rentalService.findByClient(client));
        model.addAttribute("title", "Riwayat Penyewaan");
        return "client/rentals";
    }

    @GetMapping("/landing")
    public String landing(@AuthenticationPrincipal UserPrincipal userPrincipal, Model model) {
        Client client = null;
        try {
            if (userPrincipal != null) {
                client = getCurrentClient(userPrincipal);
            }
        } catch (RuntimeException e) {
            // Client not found for the logged-in principal, or not logged in.
            // Continue with client = null for public landing page.
        }
        model.addAttribute("client", client);
        model.addAttribute("title", "Beranda");
        return "client/landing";
    }

    @GetMapping("/cars")
    public String carList(@AuthenticationPrincipal UserPrincipal userPrincipal, Model model) {
        model.addAttribute("cars", carService.getAllCars());
        model.addAttribute("isLoggedIn", userPrincipal != null);
        model.addAttribute("title", "Daftar Mobil");
        return "client/car-list";
    }

    @GetMapping("/cars/{id}")
    public String carDetails(@PathVariable Long id, @AuthenticationPrincipal UserPrincipal userPrincipal, Model model) {
        Optional<Car> carOpt = carService.getCarById(id);
        if (carOpt.isEmpty()) {
            return "redirect:/client/cars";
        }
        model.addAttribute("car", carOpt.get());
        model.addAttribute("title", carOpt.get().getMerek() + " " + carOpt.get().getModel());
        model.addAttribute("isLoggedIn", userPrincipal != null);
        return "client/car-details";
    }

    @GetMapping("/rental/{carId}")
    public String rentalForm(
            @PathVariable Long carId,
            HttpSession session,
            Model model,
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            RedirectAttributes redirectAttributes) {

        Client currentClient = null;
        if (userPrincipal == null) {
            session.setAttribute("redirectAfterLogin", "/client/rental/" + carId);
            return "redirect:/login";
        }

        try {
            currentClient = getCurrentClient(userPrincipal);
        } catch (RuntimeException e) {
            session.setAttribute("redirectAfterLogin", "/client/rental/" + carId);
            redirectAttributes.addFlashAttribute("errorMessage", "Data klien tidak ditemukan. Silakan login kembali. (" + e.getMessage() + ")");
            return "redirect:/login";
        }
        
        if (currentClient == null) {
            // Logika fallback, seharusnya jarang terpanggil jika getCurrentClient sudah benar
            session.setAttribute("redirectAfterLogin", "/client/rental/" + carId);
            redirectAttributes.addFlashAttribute("errorMessage", "Sesi klien tidak valid. Silakan login kembali.");
            return "redirect:/login";
        }

        model.addAttribute("client", currentClient);

        Optional<Car> carOpt = carService.getCarById(carId);
        if (carOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Mobil tidak ditemukan.");
            return "redirect:/client/cars";
        }
        Car selectedCar = carOpt.get();
        model.addAttribute("car", selectedCar);

        Rental rental = new Rental();
        rental.setCar(selectedCar);
        rental.setTanggalMulai(LocalDate.now());
        rental.setTanggalSelesai(LocalDate.now().plusDays(1));

        model.addAttribute("rental", rental);
        model.addAttribute("paymentMethods", PaymentMethod.values());
        model.addAttribute("title", "Formulir Penyewaan Mobil");
        return "client/rental-form";
    }

    @PostMapping("/rental/submit")
    public String submitRental(@ModelAttribute Rental rental,
                               @RequestParam("carId") Long carId,
                               @AuthenticationPrincipal UserPrincipal userPrincipal,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {
        try {
            Client client = getCurrentClient(userPrincipal); // Dapatkan client dari UserPrincipal

            Car car = carService.getCarById(carId)
                    .orElseThrow(() -> new IllegalArgumentException("Mobil dengan ID " + carId + " tidak ditemukan."));
            
            rental.setClient(client);
            rental.setCar(car);

            if (rental.getTanggalMulai() == null || rental.getTanggalSelesai() == null) {
                throw new IllegalArgumentException("Tanggal mulai dan selesai tidak boleh kosong.");
            }
            if (rental.getTanggalSelesai().isBefore(rental.getTanggalMulai())) {
                throw new IllegalArgumentException("Tanggal selesai tidak boleh lebih awal dari tanggal mulai.");
            }
            if (rental.getTanggalMulai().isBefore(LocalDate.now())) {
                throw new IllegalArgumentException("Tanggal mulai rental tidak boleh di masa lalu.");
            }

            long rentalDays = ChronoUnit.DAYS.between(rental.getTanggalMulai(), rental.getTanggalSelesai()) + 1;
            BigDecimal totalCost = car.getHargaSewa().multiply(BigDecimal.valueOf(rentalDays));
            rental.setBiayaSewa(totalCost);

            // Jika rental baru, set status ke PENDING. Status ACTIVE harus diatur oleh Agent/Admin
            if (rental.getId() == null || rental.getStatusPenyewaan() == null) {
            rental.setStatusPenyewaan(RentalStatus.ACTIVE); // Set ke PENDING saat diajukan
            }
            // Penting: Status mobil SEDANG_DISEWAKAN harus diatur ketika rental diubah menjadi ACTIVE
            // oleh Agent/Admin melalui halaman manajemen rental mereka.

            // Validasi ketersediaan mobil di service layer sebelum menyimpan
            // Pastikan isCarAvailable hanya mengecek rental yang sudah AKTIF
            if (!rentalService.isCarAvailable(car, rental.getTanggalMulai(), rental.getTanggalSelesai(), null)) {
                throw new IllegalStateException("Mobil tidak tersedia untuk periode tanggal yang dipilih. Mungkin sudah disewa oleh orang lain atau belum tersedia.");
            }

            rentalService.saveRental(rental); // Ini akan menyimpan rental dengan status PENDING

            redirectAttributes.addFlashAttribute("successMessage", "Permintaan penyewaan berhasil diajukan! Total biaya: Rp " + totalCost.toPlainString() + ". Menunggu konfirmasi admin.");
            
            return "redirect:/client/rentals/success"; // Mengarahkan ke halaman sukses yang akan di-handle oleh @GetMapping("/rentals/success")
        } catch (IllegalArgumentException | IllegalStateException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/client/rental/" + carId; // Kembali ke form dengan pesan error
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Terjadi kesalahan saat memproses permintaan: " + e.getMessage());
            e.printStackTrace(); // Penting untuk debugging
            return "redirect:/client/rental/" + carId; // Kembali ke form dengan pesan error
        }
    }

    @GetMapping("/rentals/success") // <--- Pastikan ini adalah GET Mapping yang benar untuk halaman sukses
    public String rentalSuccess(Model model) {
        System.out.println("DEBUG: rentalSuccess method hit for /client/rentals/success"); // Tambahkan log ini
        model.addAttribute("title", "Rental Confirmation");
        // model.addAttribute("successMessage", model.getAttribute("successMessage")); // Jika Anda ingin menampilkan flash attribute
        return "client/rental-success"; // Ini akan merujuk ke file template rental-success.html
    }
}