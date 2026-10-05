package com.dimdim.web;

import com.dimdim.model.Conta;
import com.dimdim.model.TipoTransacao;
import com.dimdim.model.Transacao;
import com.dimdim.service.ContaService;
import com.dimdim.service.TransacaoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.List;

@Controller
public class DashboardController {

    /** Linha do gráfico de saldo por conta (percentual relativo ao maior saldo). */
    public record SaldoConta(Long id, String numero, String titular, BigDecimal saldo, int percentual) {}

    private final ContaService contaService;
    private final TransacaoService transacaoService;

    public DashboardController(ContaService contaService, TransacaoService transacaoService) {
        this.contaService = contaService;
        this.transacaoService = transacaoService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        List<Conta> contas = contaService.listar();
        model.addAttribute("temContas", !contas.isEmpty());
        if (contas.isEmpty()) {
            return "dashboard";
        }

        List<Transacao> transacoes = transacaoService.listar();
        BigDecimal saldoTotal = soma(contas.stream().map(Conta::getSaldo).toList());
        BigDecimal depositos = totalPorTipo(transacoes, TipoTransacao.DEPOSITO);
        BigDecimal saques = totalPorTipo(transacoes, TipoTransacao.SAQUE);

        BigDecimal maior = contas.stream().map(Conta::getSaldo).max(Comparator.naturalOrder()).orElse(BigDecimal.ZERO);
        List<SaldoConta> saldos = contas.stream()
                .sorted(Comparator.comparing(Conta::getSaldo).reversed())
                .map(c -> new SaldoConta(c.getId(), c.getNumero(), c.getTitular(), c.getSaldo(), percentual(c.getSaldo(), maior)))
                .toList();

        model.addAttribute("totalContas", contas.size());
        model.addAttribute("saldoTotal", saldoTotal);
        model.addAttribute("depositos", depositos);
        model.addAttribute("saques", saques);
        model.addAttribute("qtdTransacoes", transacoes.size());
        model.addAttribute("saldos", saldos);
        model.addAttribute("ultimas", transacoes.stream().limit(5).toList());
        return "dashboard";
    }

    private BigDecimal soma(List<BigDecimal> valores) {
        return valores.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal totalPorTipo(List<Transacao> transacoes, TipoTransacao tipo) {
        return soma(transacoes.stream().filter(t -> t.getTipo() == tipo).map(Transacao::getValor).toList());
    }

    private int percentual(BigDecimal valor, BigDecimal maior) {
        if (maior.signum() <= 0) {
            return 0;
        }
        return valor.multiply(BigDecimal.valueOf(100)).divide(maior, 0, RoundingMode.HALF_UP).intValue();
    }
}
