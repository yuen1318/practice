package io.toro.pairprogramming.controllers.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/projects")
public class ProjectWebController {

    @GetMapping("/web")
    public String getFileEditor() {
        return "/project-editor.html";
    }

    @GetMapping("/projects")
    public String getProjectList() {
        return "/components/project/project-manager.html";
    }
}
