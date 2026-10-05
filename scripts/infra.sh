#!/bin/bash
# Cria toda a infraestrutura do DimDim na Azure (Azure CLI).
# Uso: bash scripts/infra.sh   (a senha do SQL e pedida sem eco, ou lida de SQL_PASS)
set -euo pipefail
cd "$(dirname "$0")"
source ./config.sh

if [ -z "${SQL_PASS:-}" ]; then
  read -r -s -p "Senha do admin SQL (min. 8, maiuscula, minuscula, numero e simbolo): " SQL_PASS
  echo
fi

echo ">> Registrando providers"
for p in Microsoft.Web Microsoft.Sql Microsoft.Insights Microsoft.OperationalInsights; do
  az provider register --namespace "$p" --wait
done
az extension add --name application-insights --only-show-errors || true

echo ">> Resource Group"
az group create --name "$RG" --location "$LOCATION" -o none

echo ">> Azure SQL Server + Database (PaaS)"
az sql server create --resource-group "$RG" --name "$SQL_SERVER" --location "$LOCATION" \
  --admin-user "$SQL_USER" --admin-password "$SQL_PASS" -o none
az sql db create --resource-group "$RG" --server "$SQL_SERVER" --name "$SQL_DB" \
  --service-objective Basic --backup-storage-redundancy Local -o none

# Libera servicos Azure (App Service) e o IP de quem esta executando (Query Editor / sqlcmd)
az sql server firewall-rule create --resource-group "$RG" --server "$SQL_SERVER" \
  --name AllowAzureServices --start-ip-address 0.0.0.0 --end-ip-address 0.0.0.0 -o none
MEU_IP=$(curl -s https://api.ipify.org)
az sql server firewall-rule create --resource-group "$RG" --server "$SQL_SERVER" \
  --name MeuIP --start-ip-address "$MEU_IP" --end-ip-address "$MEU_IP" -o none

echo ">> Log Analytics + Application Insights"
az monitor log-analytics workspace create --resource-group "$RG" --workspace-name "$LAW" \
  --location "$LOCATION" -o none
LAW_ID=$(az monitor log-analytics workspace show --resource-group "$RG" --workspace-name "$LAW" --query id -o tsv)
az monitor app-insights component create --app "$APPINSIGHTS" --resource-group "$RG" \
  --location "$LOCATION" --kind web --application-type web --workspace "$LAW_ID" -o none
AI_CONN=$(az monitor app-insights component show --app "$APPINSIGHTS" --resource-group "$RG" \
  --query connectionString -o tsv)

echo ">> App Service (Linux, Java 21)"
az appservice plan create --name "$PLAN" --resource-group "$RG" --location "$LOCATION" \
  --is-linux --sku B1 -o none
az webapp create --name "$APP" --resource-group "$RG" --plan "$PLAN" --runtime "JAVA:21-java21" -o none

echo ">> App Settings (credenciais ficam so na Azure, nunca no codigo)"
JDBC="jdbc:sqlserver://${SQL_SERVER}.database.windows.net:1433;database=${SQL_DB};encrypt=true;trustServerCertificate=false;hostNameInCertificate=*.database.windows.net;loginTimeout=30;"
az webapp config appsettings set --name "$APP" --resource-group "$RG" -o none --settings \
  SPRING_DATASOURCE_URL="$JDBC" \
  SPRING_DATASOURCE_USERNAME="$SQL_USER" \
  SPRING_DATASOURCE_PASSWORD="$SQL_PASS" \
  APPLICATIONINSIGHTS_CONNECTION_STRING="$AI_CONN" \
  ApplicationInsightsAgent_EXTENSION_VERSION="~3" \
  WEBSITES_PORT=8080

echo
echo "Infraestrutura pronta."
echo "  App:  https://${APP}.azurewebsites.net"
echo "  SQL:  ${SQL_SERVER}.database.windows.net / ${SQL_DB}"
echo "Proximo passo: executar scripts/ddl.sql no Query Editor e depois bash scripts/deploy.sh"
