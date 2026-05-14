package com.openclassrooms.Pay_My_Buddy.controller.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    @GetMapping("/login")
    public String loginPage() { return "login"; }

    @GetMapping("/register")
    public String registerPage() { return "register"; }

    @GetMapping("/home")
    public String homePage() { return "home"; }

    @GetMapping("/transfer")
    public String transferPage() { return "transfer"; }

    @GetMapping("/add-connection")
    public String addConnectionPage() { return "add-connection"; }
}