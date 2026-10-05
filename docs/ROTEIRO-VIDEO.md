# Roteiro do vídeo – DimDim (≈ 13 min, gravar em 1080p com narração)

> Dica: antes de gravar, apague o Resource Group antigo (`bash scripts/cleanup.sh`) para mostrar a criação do zero.
> Deixe o Query Editor numa aba separada e as queries prontas. Não abra a tela de App Settings com os valores visíveis (a senha aparece).

## Cena 1 – Abertura (30 s) · README no GitHub
"Olá, professor! Somos o grupo **[nome]**: [nome e RM de cada integrante]. Este é o nosso projeto do 2º Checkpoint: o **DimDim**, uma aplicação web em Java com Spring Boot que gerencia **contas bancárias e suas transações**. Ela roda no Azure App Service, persiste os dados no Azure SQL Database e é monitorada pelo Application Insights."

## Cena 2 – Arquitetura (1 min) · docs/arquitetura.svg
"Esta é a arquitetura. O usuário acessa o Web App pelo navegador. O App Service, com Java 21, conversa por JDBC com o Azure SQL Database, que é um banco PaaS e não containerizado. Nele temos duas tabelas relacionadas: **conta** e **transacao**, ligadas pela chave estrangeira conta_id. O App Service envia a telemetria ao Application Insights, que grava no Log Analytics. A infraestrutura e o deploy são feitos pelo Azure CLI, tudo dentro do resource group rg-dimdim."

## Cena 3 – Repositório, DDL e segurança (1 min 30 s) · GitHub / VS Code
"No repositório temos o código-fonte, a descrição, o diagrama, o How To no README e a pasta **scripts** com o DDL e os scripts do CLI. Este é o DDL: a tabela conta, e a tabela transacao com a chave estrangeira para conta. Reparem no application.properties: **não há nenhuma senha**. Tudo vem de variáveis de ambiente, que o script de infraestrutura configura direto no App Service."

## Cena 4 – Criação dos recursos (2 min) · terminal: `bash scripts/infra.sh`, depois Portal
"Vamos seguir o How To. Rodamos o infra.sh; ele pede a senha do banco sem exibi-la. O script cria o resource group, o servidor e o banco Azure SQL, as regras de firewall, o Log Analytics, o Application Insights, o plano do App Service e o Web App, e grava as configurações do app."
*(Pode cortar a espera na edição.)*
"Pronto. No Portal, dentro do resource group rg-dimdim, vemos todos os recursos criados."

## Cena 5 – Criação das tabelas (1 min) · Portal → SQL database → Query editor
"Entramos no Query Editor do Azure SQL e executamos o DDL. As tabelas conta e transacao foram criadas. Um SELECT em cada uma mostra que estão vazias."

## Cena 6 – Deploy (1 min 30 s) · terminal: `bash scripts/deploy.sh`, depois navegador
"Agora o deploy automatizado: o deploy.sh compila com Maven e publica o jar com `az webapp deploy`. Concluído. Esta é a aplicação rodando na nuvem, no endereço azurewebsites.net — não é localhost."

## Cena 7 – CRUD de Contas (2 min) · alternar app ↔ Query Editor após **cada** operação
- **Create:** "Criamos a conta 12345-6 do João Silva com saldo inicial de mil reais." → `SELECT * FROM conta;` "O registro está no banco." *(crie uma segunda conta)*
- **Read:** "A listagem mostra as contas gravadas."
- **Update:** "Editamos o titular para João da Silva." → SELECT "Atualizado no banco."
- **Delete:** "Excluímos a segunda conta." → SELECT "Ela sumiu da tabela."

## Cena 8 – CRUD de Transações (2 min 30 s) · alternar app ↔ Query Editor
- **Create:** "Registramos um depósito de 250 reais na conta do João; note o select com as contas, que mostra a relação." → `SELECT * FROM transacao;` + JOIN "A transação está ligada à conta pelo conta_id e o saldo da conta já foi atualizado."
- **Regra de negócio:** "Se tentarmos sacar mais que o saldo, a aplicação recusa." *(mostre a mensagem de erro)*
- **Read:** "A listagem exibe as transações com a conta e o tipo."
- **Update:** "Corrigimos o valor do depósito para 300." → SELECT "Valor atualizado e o saldo foi recalculado."
- **Delete:** "Excluímos a transação." → SELECT "Removida da tabela, e o saldo foi estornado."
- *(Opcional, 30 s)* "A API REST também está disponível; os JSONs de GET, POST, PUT e DELETE estão em api/operacoes.json."

## Cena 9 – Monitoramento (2 min) · Application Insights e métricas do SQL
"Agora o monitoramento. No **Live Metrics** vemos as requisições em tempo real enquanto navegamos. No **Application Map**, o Web App chamando o Azure SQL. Em **Performance**, o tempo de cada rota, e em **Failures**, os erros. Nos **Logs**, esta consulta lista as requisições e esta outra, as chamadas SQL feitas pela aplicação."
"No banco, as **métricas** do Azure SQL mostram o consumo de DTU e as conexões durante os nossos testes."
*(Gere tráfego alguns minutos antes de gravar esta cena; a telemetria demora a aparecer.)*

## Cena 10 – Encerramento (30 s) · README
"Mostramos a criação da infraestrutura com Azure CLI, o deploy automatizado, o CRUD nas duas tabelas com a persistência conferida no Azure SQL após cada operação e o monitoramento com o Application Insights. O passo a passo completo está no README. Obrigado, professor!"

## Checklist antes de enviar
- [ ] Vídeo em 720p ou mais, com fala; link aberto em aba anônima
- [ ] Repositório público (ou professor como colaborador)
- [ ] PDF `<nome_grupo>_webapp.pdf` com: nome do grupo, RM e nome dos integrantes, link do GitHub, link do vídeo (nada mais)
- [ ] Upload no Teams feito **só pelo representante**
- [ ] Sem senhas/tokens no código ou no histórico do Git
