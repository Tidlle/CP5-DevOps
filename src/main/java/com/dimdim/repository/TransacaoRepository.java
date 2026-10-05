package com.dimdim.repository;

import com.dimdim.model.Transacao;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TransacaoRepository extends JpaRepository<Transacao, Long> {

    @EntityGraph(attributePaths = "conta")
    List<Transacao> findAllByOrderByDataHoraDesc();

    @EntityGraph(attributePaths = "conta")
    Optional<Transacao> findWithContaById(Long id);
}
