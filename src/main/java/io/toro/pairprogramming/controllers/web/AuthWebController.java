package io.toro.pairprogramming.controllers.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class AuthWebController {

    @GetMapping("/login")
    public String getLogin() {
        return "/components/auth/login.html";
    }

    @GetMapping("/register")
    public String getRegister() {
        return "/components/auth/register.html";
    }
}
