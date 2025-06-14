package com.rental.rentalapp.controller;

import com.rental.rentalapp.model.Client;
import com.rental.rentalapp.model.Car;
import com.rental.rentalapp.service.ClientService;
import com.rental.rentalapp.service.RentalService;
import com.rental.rentalapp.service.CarService;
import com.rental.rentalapp.security.UserPrincipal; // Pastikan ini diimpor jika digunakan
import com.rental.rentalapp.model.enums.PaymentMethod; // Penting: Impor PaymentMethod

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import jakarta.servlet.http.HttpSession;

import java.util.Optional;

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
            throw new RuntimeException("Client NIK not found in session.");
        }
        return clientService.findByNik(clientNik)
                .orElseThrow(() -> new RuntimeException("Client not found for NIK: " + clientNik));
    }

    @GetMapping("/profile")
    public String profile(@AuthenticationPrincipal UserPrincipal user, HttpSession session, Model model) {
        Client client;

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
        Client client = null; // Initialize to null

        if (user != null) {
            client = getCurrentClient(user);
        } else {
            try {
                client = getCurrentClientBySession(session);
            } catch (RuntimeException e) {
                // Client not logged in, continue without client data
            }
        }
        model.addAttribute("client", client);

        return "client/landing";
    }

    @GetMapping("/cars")
    public String carList(HttpSession session, Model model) {
        // Add cars to model regardless of login status
        model.addAttribute("cars", carService.getAllCars());

        // Check if client is logged in and add this info to model
        String clientNik = (String) session.getAttribute("clientNik");
        model.addAttribute("isLoggedIn", clientNik != null);

        return "client/car-list";
    }

    @GetMapping("/cars/{id}")
    public String carDetails(@PathVariable Long id, HttpSession session, Model model) {
        var carOpt = carService.getCarById(id);
        if (carOpt.isEmpty()) {
            return "redirect:/client/cars";
        }

        model.addAttribute("car", carOpt.get());

        // Check if client is logged in and add this info to model (still useful for header/other elements)
        String clientNik = (String) session.getAttribute("clientNik");
        model.addAttribute("isLoggedIn", clientNik != null);

        return "client/car-details";
    }

    // THIS IS THE MODIFIED METHOD
  @GetMapping("/rental/{carId}")
    public String rentalForm(
            @PathVariable Long carId,
            HttpSession session,
            Model model,
            @AuthenticationPrincipal(expression = "client") Client currentClient) { // <-- KEY CHANGE HERE

        // If currentClient is null, it means:
        // 1. No user is logged in.
        // 2. The logged-in user's principal is not a UserPrincipal, OR
        // 3. The UserPrincipal does not return a Client object via getClient() (e.g., it's an Agent, or getClient() returns null).
        if (currentClient == null) {
            // Store the target URL in session for redirection after successful login
            session.setAttribute("redirectAfterLogin", "/client/rental/" + carId);
            return "redirect:/login"; // Redirect to the login page
        }

        // If a Client object is successfully injected (user is logged in as a Client)
        model.addAttribute("client", currentClient); // Add the Client object to the model

        // Get Car details
        Optional<Car> carOpt = carService.getCarById(carId);
        if (carOpt.isEmpty()) {
            return "redirect:/client/cars"; // Car not found, redirect to car list
        }
        model.addAttribute("car", carOpt.get());

        // Add Payment Methods for the dropdown
        model.addAttribute("paymentMethods", PaymentMethod.values());

        return "client/rental-form"; // Proceed to the rental form
    }
    // You will need to add a @PostMapping method for /client/rental/submit here
    // Example (simplified):
    // @PostMapping("/rental/submit")
    // public String submitRental(@RequestParam Long carId,
    //                            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate tanggalMulai,
    //                            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate tanggalSelesai,
    //                            @RequestParam PaymentMethod metodePembayaran,
    //                            HttpSession session, Model model) {
    //     try {
    //         Client client = getCurrentClientBySession(session); // Get logged in client
    //         Car car = carService.getCarById(carId).orElseThrow(() -> new RuntimeException("Car not found"));
    //
    //         // Implement rental logic here (create Rental object, save to DB, update car status)
    //         // Example: rentalService.createRental(client, car, tanggalMulai, tanggalSelesai, metodePembayaran);
    //
    //         return "redirect:/client/rentals"; // Redirect to rental history or a success page
    //     } catch (Exception e) {
    //         model.addAttribute("error", "Gagal memproses penyewaan: " + e.getMessage());
    //         // You might want to reload the rental form with error message
    //         return rentalForm(carId, session, model, null); // Pass null for UserPrincipal to avoid re-authentication issues
    //     }
    // }
}