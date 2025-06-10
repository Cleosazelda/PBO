package com.rental.rentalapp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String landingPage() {
        return "public/landing"; // thymeleaf template landing.html
    }
}
