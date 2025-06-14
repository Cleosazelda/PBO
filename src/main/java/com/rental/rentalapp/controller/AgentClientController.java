package com.rental.rentalapp.controller;

import com.rental.rentalapp.model.Client;
import com.rental.rentalapp.service.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/agent/clients")
public class AgentClientController {

    private final ClientService clientService;

    @Autowired
    public AgentClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    // Tampilkan semua client
    @GetMapping
    public String listClients(Model model) {
        model.addAttribute("clients", clientService.getAllClients());
        return "agent/clients"; // Mengarah ke templates/agent/clients.html
    }

    // Form untuk tambah client baru
    @GetMapping("/add")
    public String showClientForm(Model model) {
        model.addAttribute("client", new Client());
        return "agent/client-form"; // Mengarah ke templates/agent/client-form.html
    }

    // Form untuk edit client yang sudah ada
    @GetMapping("/edit/{nik}")
    public String showEditClientForm(@PathVariable("nik") String nik, Model model) {
        Client client = clientService.findByNik(nik).orElse(null);
        if (client == null) {
            return "redirect:/agent/clients";
        }
        model.addAttribute("client", client);
        return "agent/client-form";
    }

    // Simpan client (baru atau hasil edit)
    @PostMapping("/save")
    public String saveClient(@ModelAttribute("client") Client client) {
        clientService.saveClient(client);
        return "redirect:/agent/clients";
    }

    // Hapus client
    @GetMapping("/delete/{nik}")
    public String deleteClient(@PathVariable("nik") String nik) {
        clientService.deleteClient(nik);
        return "redirect:/agent/clients";
    }
}
