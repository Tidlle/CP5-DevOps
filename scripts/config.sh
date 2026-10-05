#!/bin/bash
# Variaveis compartilhadas pelos scripts. NAO colocar senhas aqui.
# Troque SUFIXO (ex.: RM do representante em minusculas) para gerar nomes globalmente unicos.
SUFIXO="rm562259"

# Regioes liberadas pela politica da assinatura: southafricanorth chilecentral centralus mexicocentral eastus
LOCATION="centralus"            # regiao do Resource Group, App Service e monitoramento
# Regioes tentadas, em ordem, para o Azure SQL (eastus esta sem vaga para novos servidores SQL)
SQL_LOCATIONS=(centralus mexicocentral chilecentral southafricanorth eastus)
RG="rg-dimdim"
PLAN="plan-dimdim"
APP="dimdim-${SUFIXO}"
SQL_SERVER="sqlsrv-dimdim-${SUFIXO}"
SQL_DB="dimdimdb"
SQL_USER="dimdimadmin"
LAW="law-dimdim"
APPINSIGHTS="ai-dimdim"
