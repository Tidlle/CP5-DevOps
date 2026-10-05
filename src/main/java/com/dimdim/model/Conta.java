package com.dimdim.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "conta")
public class Conta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Informe o número da conta")
    @Size(max = 20)
    @Column(nullable = false, unique = true, length = 20)
    private String numero;

    @NotBlank(message = "Informe a agência")
    @Size(max = 10)
    @Column(nullable = false, length = 10)
    private String agencia;

    @NotBlank(message = "Informe o titular")
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String titular;

    @NotNull(message = "Informe o saldo inicial")
    @DecimalMin(value = "0.00", message = "O saldo não pode ser negativo")
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal saldo = BigDecimal.ZERO;

    @JsonIgnore
    @OneToMany(mappedBy = "conta", cascade = CascadeType.REMOVE)
    private List<Transacao> transacoes = new ArrayList<>();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNumero() { return numero; }
    public void setNumero(String numero) { this.numero = numero; }

    public String getAgencia() { return agencia; }
    public void setAgencia(String agencia) { this.agencia = agencia; }

    public String getTitular() { return titular; }
    public void setTitular(String titular) { this.titular = titular; }

    public BigDecimal getSaldo() { return saldo; }
    public void setSaldo(BigDecimal saldo) { this.saldo = saldo; }

    public List<Transacao> getTransacoes() { return transacoes; }
    public void setTransacoes(List<Transacao> transacoes) { this.transacoes = transacoes; }
}
