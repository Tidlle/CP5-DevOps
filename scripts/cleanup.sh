#!/bin/bash
# Remove TODOS os recursos (usar so depois da correcao, para nao gastar creditos).
set -euo pipefail
cd "$(dirname "$0")"
source ./config.sh
az group delete --name "$RG" --yes --no-wait
