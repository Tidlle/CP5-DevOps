package com.dimdim.model;

public enum TipoTransacao {
    DEPOSITO("Depósito"),
    SAQUE("Saque");

    private final String descricao;

    TipoTransacao(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
