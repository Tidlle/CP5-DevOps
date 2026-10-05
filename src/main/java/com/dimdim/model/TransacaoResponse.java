package com.dimdim.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** Representação JSON de uma transação (evita serializar a conta inteira). */
public record TransacaoResponse(Long id, TipoTransacao tipo, BigDecimal valor, String descricao,
                                LocalDateTime dataHora, Long contaId) {

    public static TransacaoResponse de(Transacao t) {
        return new TransacaoResponse(t.getId(), t.getTipo(), t.getValor(), t.getDescricao(),
                t.getDataHora(), t.getConta().getId());
    }
}
