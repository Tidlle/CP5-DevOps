package com.dimdim.service;

import com.dimdim.model.Conta;
import com.dimdim.repository.ContaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ContaService {

    private final ContaRepository repository;

    public ContaService(ContaRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<Conta> listar() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public Conta buscar(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Conta " + id + " não encontrada"));
    }

    @Transactional
    public Conta criar(Conta conta) {
        if (repository.existsByNumero(conta.getNumero())) {
            throw new RegraNegocioException("Já existe uma conta com o número " + conta.getNumero());
        }
        conta.setId(null);
        return repository.save(conta);
    }

    /** O saldo não é alterado aqui: ele só muda por meio de transações. */
    @Transactional
    public Conta atualizar(Long id, Conta dados) {
        Conta conta = buscar(id);
        if (repository.existsByNumeroAndIdNot(dados.getNumero(), id)) {
            throw new RegraNegocioException("Já existe uma conta com o número " + dados.getNumero());
        }
        conta.setNumero(dados.getNumero());
        conta.setAgencia(dados.getAgencia());
        conta.setTitular(dados.getTitular());
        return conta;
    }

    /** Remove a conta e, em cascata, as transações dela. */
    @Transactional
    public void excluir(Long id) {
        repository.delete(buscar(id));
    }
}
