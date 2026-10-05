package com.dimdim.web;

import com.dimdim.model.Conta;
import com.dimdim.service.ContaService;
import com.dimdim.service.RegraNegocioException;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ContaController {

    private final ContaService service;

    public ContaController(ContaService service) {
        this.service = service;
    }

    @GetMapping("/contas")
    public String listar(Model model) {
        model.addAttribute("contas", service.listar());
        return "contas/lista";
    }

    @GetMapping("/contas/novo")
    public String novo(Model model) {
        model.addAttribute("conta", new Conta());
        return "contas/form";
    }

    @PostMapping("/contas")
    public String criar(@Valid @ModelAttribute("conta") Conta conta, BindingResult result,
                        RedirectAttributes redirect) {
        if (!result.hasErrors()) {
            try {
                service.criar(conta);
                redirect.addFlashAttribute("sucesso", "Conta criada com sucesso");
                return "redirect:/contas";
            } catch (RegraNegocioException e) {
                result.rejectValue("numero", "duplicado", e.getMessage());
            }
        }
        return "contas/form";
    }

    @GetMapping("/contas/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        model.addAttribute("conta", service.buscar(id));
        return "contas/form";
    }

    @PostMapping("/contas/{id}")
    public String atualizar(@PathVariable Long id, @Valid @ModelAttribute("conta") Conta conta,
                            BindingResult result, RedirectAttributes redirect) {
        if (!result.hasErrors()) {
            try {
                service.atualizar(id, conta);
                redirect.addFlashAttribute("sucesso", "Conta atualizada com sucesso");
                return "redirect:/contas";
            } catch (RegraNegocioException e) {
                result.rejectValue("numero", "duplicado", e.getMessage());
            }
        }
        conta.setId(id);
        return "contas/form";
    }

    @PostMapping("/contas/{id}/excluir")
    public String excluir(@PathVariable Long id, RedirectAttributes redirect) {
        service.excluir(id);
        redirect.addFlashAttribute("sucesso", "Conta excluída (e suas transações)");
        return "redirect:/contas";
    }
}
