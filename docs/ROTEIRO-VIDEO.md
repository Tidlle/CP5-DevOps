# Roteiro do vídeo – DimDim (≈ 15 min, gravar em 1080p com narração)

> Dica: antes de gravar, apague o Resource Group antigo (`bash scripts/cleanup.sh`) para mostrar a criação do zero.
> Deixe o Query Editor numa aba separada e as queries prontas. Não abra a tela de App Settings com os valores visíveis (a senha aparece).
> Antes de gravar, confirme que `application.properties` e `pom.xml` estão na configuração da Azure (sem o H2 de teste local).

## Cena 1 – Abertura (30 s) · README no GitHub
"Olá, professor! Somos o grupo **[nome]**: [nome e RM de cada integrante]. Este é o nosso projeto do 2º Checkpoint: o **DimDim**, uma aplicação web em Java com Spring Boot que gerencia **contas bancárias e suas transações**. Ela roda no Azure App Service, persiste os dados no Azure SQL Database e é monitorada pelo Application Insights."

## Cena 2 – Arquitetura (1 min) · imagem da arquitetura
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
"Agora o deploy automatizado: o deploy.sh compila com Maven e publica o jar com `az webapp deploy`. Concluído."

## Cena 7 – Landing page e dashboard vazio (1 min) · navegador no endereço azurewebsites.net
"Esta é a aplicação rodando na nuvem, no endereço azurewebsites.net, e não em localhost. A rota padrão é a **landing page**, que explica o que o DimDim faz: gerenciar contas, registrar depósitos e saques, aplicar regras de segurança e acompanhar tudo em um dashboard."
*(Role a página mostrando os cartões e os 3 passos.)*
"Como ainda não existe nenhuma conta, o botão principal é **Criar minha conta**. Se abrirmos o **Dashboard** agora, ele mostra um estado vazio, convidando a criar a primeira conta."

## Cena 8 – CRUD de Contas (2 min) · alternar app ↔ Query Editor após **cada** operação
- **Create:** "Pelo botão Criar minha conta, cadastramos a conta 12345-6 do João Silva com saldo inicial de mil reais." → `SELECT * FROM conta;` "O registro está no banco." *(crie uma segunda conta: 98765-4, Maria Souza, 400 reais)*
- **Read:** "A listagem mostra as contas gravadas."
- **Update:** "Editamos o titular da Maria para Maria de Souza. Repare que o saldo fica bloqueado: ele só muda por transações." → SELECT "Atualizado no banco."
- **Delete:** *(Deixe a exclusão para o fim, na Cena 10, para o dashboard ter dados; ou crie uma terceira conta só para excluir agora.)* "Excluímos esta conta de teste." → SELECT "Ela sumiu da tabela."

## Cena 9 – CRUD de Transações (2 min 30 s) · alternar app ↔ Query Editor
- **Create:** "Registramos um depósito de 250 reais na conta do João; note o campo de seleção com as contas, que mostra a relação." → `SELECT * FROM transacao;` + JOIN "A transação está ligada à conta pelo conta_id e o saldo da conta já foi atualizado."
- **Regra de negócio:** "Se tentarmos sacar mais que o saldo, a aplicação recusa." *(mostre a mensagem de erro)*
- *(Crie também um saque válido de 80 reais na conta da Maria.)*
- **Read:** "A listagem exibe as transações com a conta e o tipo."
- **Update:** "Corrigimos o valor do depósito para 300." → SELECT "Valor atualizado e o saldo foi recalculado."
- **Delete:** "Excluímos uma transação de teste." → SELECT "Removida da tabela, e o saldo foi estornado."
- *(Opcional, 30 s)* "A API REST também está disponível; os JSONs de GET, POST, PUT e DELETE estão em api/operacoes.json."

## Cena 10 – Dashboard (1 min) · navegador → Dashboard, depois Query Editor
"Com as contas criadas e as transações registradas, o **Dashboard** passa a mostrar os dados reais: o **saldo total**, a quantidade de contas, o total de **entradas**, que são os depósitos, e de **saídas**, que são os saques. Abaixo, o **saldo por conta** em barras e as **últimas movimentações**."
*(No Query Editor:)* "Estes números vêm direto do Azure SQL. Se somarmos os saldos da tabela conta, temos o mesmo valor do dashboard."
```sql
SELECT SUM(saldo) AS saldo_total FROM conta;
```
*(Se quiser mostrar a exclusão de conta com cascata: excluir uma conta pela tela e mostrar no banco que as transações dela também saíram da tabela `transacao`.)*

## Cena 11 – Monitoramento (2 min) · Application Insights e métricas do SQL
"Agora o monitoramento. No **Live Metrics** vemos as requisições em tempo real enquanto navegamos. No **Application Map**, o Web App chamando o Azure SQL. Em **Performance**, o tempo de cada rota, e em **Failures**, os erros. Nos **Logs**, esta consulta lista as requisições e esta outra, as chamadas SQL feitas pela aplicação."
"No banco, as **métricas** do Azure SQL mostram o consumo de DTU e as conexões durante os nossos testes."
*(Gere tráfego alguns minutos antes de gravar esta cena; a telemetria demora a aparecer.)*
```kusto
requests | order by timestamp desc | take 20
dependencies | where type has "SQL" | order by timestamp desc | take 20
```

## Cena 12 – Encerramento (30 s) · README
"Mostramos a criação da infraestrutura com Azure CLI, o deploy automatizado, a landing page e o dashboard, o CRUD nas duas tabelas com a persistência conferida no Azure SQL após cada operação e o monitoramento com o Application Insights. O passo a passo completo está no README. Obrigado, professor!"

## Checklist antes de enviar
- [ ] Vídeo em 720p ou mais, com fala; link aberto em aba anônima
- [ ] Repositório público (ou professor como colaborador)
- [ ] PDF `<nome_grupo>_webapp.pdf` com: nome do grupo, RM e nome dos integrantes, link do GitHub, link do vídeo (nada mais)
- [ ] Upload no Teams feito **só pelo representante**
- [ ] Sem senhas/tokens no código ou no histórico do Git
