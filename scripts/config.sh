#!/bin/bash
# Variaveis compartilhadas pelos scripts. NAO colocar senhas aqui.
# Troque SUFIXO (ex.: RM do representante em minusculas) para gerar nomes globalmente unicos.
SUFIXO="rm562259"

LOCATION="brazilsouth"          # se o SQL der erro de regiao/quota, use "eastus2"
RG="rg-dimdim"
PLAN="plan-dimdim"
APP="dimdim-${SUFIXO}"
SQL_SERVER="sqlsrv-dimdim-${SUFIXO}"
SQL_DB="dimdimdb"
SQL_USER="dimdimadmin"
LAW="law-dimdim"
APPINSIGHTS="ai-dimdim"
