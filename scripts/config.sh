#!/bin/bash
# Variaveis compartilhadas pelos scripts. NAO colocar senhas aqui.
# Troque SUFIXO (ex.: RM do representante em minusculas) para gerar nomes globalmente unicos.
SUFIXO="rm562259"

LOCATION="brazilsouth"          # regiao do Resource Group, App Service e monitoramento
# Regioes tentadas, em ordem, para o Azure SQL (algumas ficam bloqueadas por politica ou sem vaga)
SQL_LOCATIONS=(brazilsouth eastus2 westus3 centralus westus2 northcentralus southcentralus canadacentral eastus)
RG="rg-dimdim"
PLAN="plan-dimdim"
APP="dimdim-${SUFIXO}"
SQL_SERVER="sqlsrv-dimdim-${SUFIXO}"
SQL_DB="dimdimdb"
SQL_USER="dimdimadmin"
LAW="law-dimdim"
APPINSIGHTS="ai-dimdim"
