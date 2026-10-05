package com.dimdim.web;

import com.dimdim.service.ContaService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private final ContaService contaService;

    public HomeController(ContaService contaService) {
        this.contaService = contaService;
    }

    /** Landing page: apresenta o que a aplicação faz. */
    @GetMapping("/")
    public String landing(Model model) {
        model.addAttribute("temContas", !contaService.listar().isEmpty());
        return "landing";
    }
}
