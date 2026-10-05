package com.dimdim.web;

import com.dimdim.model.TipoTransacao;
import com.dimdim.model.Transacao;
import com.dimdim.model.TransacaoForm;
import com.dimdim.service.ContaService;
import com.dimdim.service.RegraNegocioException;
import com.dimdim.service.TransacaoService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/transacoes")
public class TransacaoController {

    private final TransacaoService service;
    private final ContaService contaService;

    public TransacaoController(TransacaoService service, ContaService contaService) {
        this.service = service;
        this.contaService = contaService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("transacoes", service.listar());
        return "transacoes/lista";
    }

    @GetMapping("/novo")
    public String novo(@RequestParam(required = false) Long contaId, Model model) {
        TransacaoForm form = new TransacaoForm();
        form.setContaId(contaId);
        return formulario(model, form, null);
    }

    @PostMapping
    public String criar(@Valid @ModelAttribute("form") TransacaoForm form, BindingResult result,
                        Model model, RedirectAttributes redirect) {
        if (!result.hasErrors()) {
            try {
                service.criar(form);
                redirect.addFlashAttribute("sucesso", "Transação registrada com sucesso");
                return "redirect:/transacoes";
            } catch (RegraNegocioException e) {
                model.addAttribute("erro", e.getMessage());
            }
        }
        return formulario(model, form, null);
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        Transacao t = service.buscar(id);
        TransacaoForm form = new TransacaoForm();
        form.setTipo(t.getTipo());
        form.setValor(t.getValor());
        form.setDescricao(t.getDescricao());
        form.setContaId(t.getConta().getId());
        return formulario(model, form, id);
    }

    @PostMapping("/{id}")
    public String atualizar(@PathVariable Long id, @Valid @ModelAttribute("form") TransacaoForm form,
                            BindingResult result, Model model, RedirectAttributes redirect) {
        if (!result.hasErrors()) {
            try {
                service.atualizar(id, form);
                redirect.addFlashAttribute("sucesso", "Transação atualizada com sucesso");
                return "redirect:/transacoes";
            } catch (RegraNegocioException e) {
                model.addAttribute("erro", e.getMessage());
            }
        }
        return formulario(model, form, id);
    }

    @PostMapping("/{id}/excluir")
    public String excluir(@PathVariable Long id, RedirectAttributes redirect) {
        try {
            service.excluir(id);
            redirect.addFlashAttribute("sucesso", "Transação excluída e saldo estornado");
        } catch (RegraNegocioException e) {
            redirect.addFlashAttribute("erro", e.getMessage());
        }
        return "redirect:/transacoes";
    }

    private String formulario(Model model, TransacaoForm form, Long id) {
        model.addAttribute("form", form);
        model.addAttribute("transacaoId", id);
        model.addAttribute("contas", contaService.listar());
        model.addAttribute("tipos", TipoTransacao.values());
        return "transacoes/form";
    }
}
