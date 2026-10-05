package com.dimdim.service;

import com.dimdim.model.Conta;
import com.dimdim.model.TipoTransacao;
import com.dimdim.model.Transacao;
import com.dimdim.model.TransacaoForm;
import com.dimdim.repository.TransacaoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class TransacaoService {

    private final TransacaoRepository repository;
    private final ContaService contaService;

    public TransacaoService(TransacaoRepository repository, ContaService contaService) {
        this.repository = repository;
        this.contaService = contaService;
    }

    @Transactional(readOnly = true)
    public List<Transacao> listar() {
        return repository.findAllByOrderByDataHoraDesc();
    }

    @Transactional(readOnly = true)
    public Transacao buscar(Long id) {
        return repository.findWithContaById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Transação " + id + " não encontrada"));
    }

    @Transactional
    public Transacao criar(TransacaoForm form) {
        Conta conta = contaService.buscar(form.getContaId());
        aplicar(conta, form.getTipo(), form.getValor());

        Transacao t = new Transacao();
        preencher(t, form, conta);
        return repository.save(t);
    }

    /** Estorna o efeito antigo no saldo e aplica o novo (a conta pode ter mudado). */
    @Transactional
    public Transacao atualizar(Long id, TransacaoForm form) {
        Transacao t = buscar(id);
        estornar(t.getConta(), t.getTipo(), t.getValor());

        Conta conta = contaService.buscar(form.getContaId());
        aplicar(conta, form.getTipo(), form.getValor());

        preencher(t, form, conta);
        return t;
    }

    @Transactional
    public void excluir(Long id) {
        Transacao t = buscar(id);
        estornar(t.getConta(), t.getTipo(), t.getValor());
        repository.delete(t);
    }

    private void preencher(Transacao t, TransacaoForm form, Conta conta) {
        t.setTipo(form.getTipo());
        t.setValor(form.getValor());
        t.setDescricao(form.getDescricao());
        t.setConta(conta);
    }

    private void aplicar(Conta conta, TipoTransacao tipo, BigDecimal valor) {
        if (tipo == TipoTransacao.DEPOSITO) {
            conta.setSaldo(conta.getSaldo().add(valor));
        } else {
            semSaldoNegativo(conta, conta.getSaldo().subtract(valor), "Saldo insuficiente para o saque");
            conta.setSaldo(conta.getSaldo().subtract(valor));
        }
    }

    private void estornar(Conta conta, TipoTransacao tipo, BigDecimal valor) {
        if (tipo == TipoTransacao.DEPOSITO) {
            semSaldoNegativo(conta, conta.getSaldo().subtract(valor),
                    "Não é possível remover/alterar este depósito: o saldo da conta ficaria negativo");
            conta.setSaldo(conta.getSaldo().subtract(valor));
        } else {
            conta.setSaldo(conta.getSaldo().add(valor));
        }
    }

    private void semSaldoNegativo(Conta conta, BigDecimal novoSaldo, String mensagem) {
        if (novoSaldo.signum() < 0) {
            throw new RegraNegocioException(mensagem + " (conta " + conta.getNumero() + ")");
        }
    }
}
