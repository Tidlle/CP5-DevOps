-- DimDim - DDL (Azure SQL Database)
-- Executar no Query Editor do Portal Azure (ou via sqlcmd) ANTES do deploy da aplicacao.

CREATE TABLE conta (
    id      BIGINT IDENTITY(1,1) PRIMARY KEY,
    numero  VARCHAR(20)   NOT NULL,
    agencia VARCHAR(10)   NOT NULL,
    titular VARCHAR(100)  NOT NULL,
    saldo   DECIMAL(15,2) NOT NULL DEFAULT 0,
    CONSTRAINT uq_conta_numero UNIQUE (numero),
    CONSTRAINT ck_conta_saldo  CHECK (saldo >= 0)
);

CREATE TABLE transacao (
    id        BIGINT IDENTITY(1,1) PRIMARY KEY,
    tipo      VARCHAR(20)   NOT NULL,              -- DEPOSITO | SAQUE
    valor     DECIMAL(15,2) NOT NULL,
    descricao VARCHAR(200)  NULL,
    data_hora DATETIME2     NOT NULL DEFAULT SYSDATETIME(),
    conta_id  BIGINT        NOT NULL,
    CONSTRAINT ck_transacao_tipo  CHECK (tipo IN ('DEPOSITO', 'SAQUE')),
    CONSTRAINT ck_transacao_valor CHECK (valor > 0),
    CONSTRAINT fk_transacao_conta FOREIGN KEY (conta_id)
        REFERENCES conta (id) ON DELETE CASCADE
);

CREATE INDEX ix_transacao_conta ON transacao (conta_id);
