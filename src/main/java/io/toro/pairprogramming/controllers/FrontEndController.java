package io.toro.pairprogramming.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class FrontEndController {

    @GetMapping(value = "chat")
    public String chat(){
        return "cht.html";
    }

    @GetMapping(value = "")
    public String login(){
        return "index.html";
    }

    @GetMapping(value = "/account")
    public String account(){
        return "user_account.html";
    }

    @GetMapping(value = "/projects")
    public String project_manager(){
        return "project-manager.html";
    }

    @GetMapping(value = "/match")
    public String ggg(){
        return "match_users.html";
    }

}
