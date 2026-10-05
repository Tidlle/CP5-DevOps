# DimDim – Contas e Transações (CP2 · DevOps Tools & Cloud Computing)

**Grupo:** _<nome do grupo>_ · **Integrantes:** _<RM – Nome>_ · **Vídeo:** _<link do vídeo>_

## 1. Descrição da solução

O **DimDim** é uma aplicação web (Java 21 + Spring Boot + Thymeleaf) para gerenciar **contas** bancárias e suas **transações** (depósitos e saques).

- **Contas**: cadastro, listagem, edição e exclusão (número, agência, titular, saldo).
- **Transações**: cada transação pertence a uma conta. Depósitos somam e saques subtraem do saldo da conta; saque sem saldo é recusado; editar/excluir uma transação estorna o efeito no saldo; excluir uma conta remove suas transações.
- Telas HTML (front-end) e também API REST em `/api/contas` e `/api/transacoes` (exemplos em [`api/operacoes.json`](api/operacoes.json)).
- Persistência em **Azure SQL Database** (PaaS) com duas tabelas relacionadas (`conta` 1 — N `transacao`), com CRUD completo em ambas.
- Deploy automatizado com **Azure CLI** (`az webapp deploy`) e monitoramento com **Application Insights**.

## 2. Arquitetura

![Arquitetura](docs/arquitetura.svg)

| Recurso Azure | Função |
|---|---|
| App Service (Linux, Java 21, plano B1) | Hospeda a aplicação |
| Azure SQL Database (Basic) | Banco PaaS com as tabelas `conta` e `transacao` |
| Application Insights + Log Analytics | Telemetria (requests, dependências SQL, falhas) |

## 3. Estrutura do repositório

```
├── README.md
├── docs/arquitetura.drawio  diagrama editável (draw.io)
├── docs/arquitetura.svg     diagrama exportado (exibido no README)
├── scripts/
│   ├── ddl.sql        DDL das tabelas
│   ├── config.sh      nomes dos recursos (sem segredos)
│   ├── infra.sh       cria os recursos na Azure (CLI)
│   ├── deploy.sh      build + az webapp deploy
│   └── cleanup.sh     remove tudo
├── api/operacoes.json exemplos GET/POST/PUT/DELETE
└── src/               código-fonte (Spring Boot)
```

## 4. How To – implantação na Azure (passo a passo)

### Pré-requisitos
JDK 21, Maven 3.9+, [Azure CLI](https://learn.microsoft.com/cli/azure/install-azure-cli), Git Bash (os scripts são `bash`) e uma assinatura Azure.

### Passo 1 – Login e configuração
```bash
az login
az account show
git clone <url-deste-repositorio> && cd <pasta>
```
Edite `scripts/config.sh` e troque `SUFIXO` (ex.: seu RM) para que os nomes fiquem únicos no mundo.

### Passo 2 – Criar a infraestrutura
```bash
bash scripts/infra.sh
```
O script pede a senha do admin SQL (sem eco) e cria: Resource Group, SQL Server + Database, regras de firewall, Log Analytics, Application Insights, App Service Plan e Web App. As credenciais ficam **somente** nos *App Settings* do Web App (`SPRING_DATASOURCE_*`), nunca no código.

> Se a região `brazilsouth` recusar o SQL, altere `LOCATION` em `config.sh` (ex.: `eastus2`) e rode de novo.

### Passo 3 – Criar as tabelas
Portal Azure → **SQL databases → dimdimdb → Query editor** → login com o usuário/senha do passo 2 → colar o conteúdo de [`scripts/ddl.sql`](scripts/ddl.sql) → **Run**.

### Passo 4 – Deploy
```bash
bash scripts/deploy.sh
```
Compila com Maven e publica o `dimdim.jar` com `az webapp deploy`. A aplicação fica em `https://dimdim-<SUFIXO>.azurewebsites.net` (a primeira carga pode levar ~1 min).

### Passo 5 – Testar o CRUD e conferir o banco
Use o app (menus **Contas** e **Transações**) e, após cada operação, confira no Query Editor:
```sql
SELECT * FROM conta;
SELECT * FROM transacao;
SELECT t.id, t.tipo, t.valor, c.numero, c.titular, c.saldo
FROM transacao t JOIN conta c ON c.id = t.conta_id;
```

### Passo 6 – Monitoramento
No recurso **ai-dimdim** (Application Insights): *Live Metrics*, *Application map*, *Performance*, *Failures* e *Logs*:
```kusto
requests | order by timestamp desc | take 20
dependencies | where type has "SQL" | order by timestamp desc | take 20
exceptions | order by timestamp desc | take 20
```
No banco: **dimdimdb → Monitoring → Metrics** (DTU percentage, Connections successful). A telemetria leva alguns minutos para aparecer.

### Passo 7 – Limpeza
```bash
bash scripts/cleanup.sh
```

## 5. Rodando localmente (opcional, só para desenvolvimento)

Defina `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME` e `SPRING_DATASOURCE_PASSWORD` apontando para um Azure SQL (com seu IP liberado no firewall) e execute `mvn spring-boot:run`. Os testes (`mvn test`) usam H2 em memória e não precisam de nada.

## 6. API REST

Resumo (corpos de exemplo em [`api/operacoes.json`](api/operacoes.json)):

| Método | Rota | Descrição |
|---|---|---|
| GET / POST | `/api/contas` | Lista / cria conta |
| GET / PUT / DELETE | `/api/contas/{id}` | Busca / atualiza / remove conta |
| GET / POST | `/api/transacoes` | Lista / cria transação |
| GET / PUT / DELETE | `/api/transacoes/{id}` | Busca / atualiza / remove transação |

## 7. Segurança
Nenhum usuário, senha ou token está no repositório: o `application.properties` lê tudo de variáveis de ambiente e o `infra.sh` pede a senha em tempo de execução.
