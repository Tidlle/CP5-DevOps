package com.dimdim;

import com.dimdim.model.Conta;
import com.dimdim.model.TipoTransacao;
import com.dimdim.model.Transacao;
import com.dimdim.model.TransacaoForm;
import com.dimdim.service.ContaService;
import com.dimdim.service.RegraNegocioException;
import com.dimdim.service.TransacaoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class TransacaoServiceTest {

    @Autowired ContaService contas;
    @Autowired TransacaoService transacoes;

    private Conta novaConta(String numero, String saldo) {
        Conta c = new Conta();
        c.setNumero(numero);
        c.setAgencia("0001");
        c.setTitular("Teste");
        c.setSaldo(new BigDecimal(saldo));
        return contas.criar(c);
    }

    private TransacaoForm form(Long contaId, TipoTransacao tipo, String valor) {
        TransacaoForm f = new TransacaoForm();
        f.setContaId(contaId);
        f.setTipo(tipo);
        f.setValor(new BigDecimal(valor));
        return f;
    }

    @Test
    void ciclo_completo_atualiza_saldo() {
        Conta c = novaConta("T-1", "100.00");

        Transacao dep = transacoes.criar(form(c.getId(), TipoTransacao.DEPOSITO, "50.00"));
        assertEquals(0, new BigDecimal("150.00").compareTo(contas.buscar(c.getId()).getSaldo()));

        transacoes.atualizar(dep.getId(), form(c.getId(), TipoTransacao.DEPOSITO, "20.00"));
        assertEquals(0, new BigDecimal("120.00").compareTo(contas.buscar(c.getId()).getSaldo()));

        transacoes.excluir(dep.getId());
        assertEquals(0, new BigDecimal("100.00").compareTo(contas.buscar(c.getId()).getSaldo()));
    }

    @Test
    void saque_sem_saldo_e_recusado() {
        Conta c = novaConta("T-2", "10.00");
        assertThrows(RegraNegocioException.class,
                () -> transacoes.criar(form(c.getId(), TipoTransacao.SAQUE, "10.01")));
    }

    @Test
    void excluir_conta_remove_transacoes() {
        Conta c = novaConta("T-3", "0.00");
        transacoes.criar(form(c.getId(), TipoTransacao.DEPOSITO, "5.00"));
        contas.excluir(c.getId());
        assertTrue(transacoes.listar().stream().noneMatch(t -> t.getConta().getId().equals(c.getId())));
    }

    @Test
    void numero_duplicado_e_recusado() {
        novaConta("T-4", "0.00");
        assertThrows(RegraNegocioException.class, () -> novaConta("T-4", "0.00"));
    }
}
