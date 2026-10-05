#!/bin/bash
# Compila e publica o DimDim no Azure App Service (Azure CLI: az webapp deploy).
set -euo pipefail
cd "$(dirname "$0")/.."
source ./scripts/config.sh

echo ">> Build"
mvn -B clean package -DskipTests

echo ">> Deploy"
az webapp deploy --resource-group "$RG" --name "$APP" --src-path target/dimdim.jar --type jar

echo ">> Publicado: https://${APP}.azurewebsites.net"
