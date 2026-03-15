#!/bin/bash
# Script de test pour le projet ChriOnline

echo "========================================"
echo "ChriOnline - Execution des tests"
echo "========================================"
echo ""

# Se placer dans le répertoire du projet
cd "$(dirname "$0")"

# Vérifier que Maven est disponible
if ! command -v ./mvnw &> /dev/null; then
    echo "ERREUR: Maven n'est pas trouvé dans le PATH"
    echo "Veuillez installer Maven ou l'ajouter au PATH"
    exit 1
fi

echo "Maven trouvé:"
./mvnw --version
echo ""

# Nettoyer et compiler
echo "Compilation du projet..."
./mvnw clean compile -q
if [ $? -ne 0 ]; then
    echo "ERREUR lors de la compilation"
    exit 1
fi
echo "[OK] Compilation réussie"
echo ""

# Exécuter les tests
echo "Exécution des tests unitaires..."
./mvnw test

if [ $? -eq 0 ]; then
    echo ""
    echo "========================================"
    echo "Tests exécutés avec succès !"
    echo "========================================"
else
    echo ""
    echo "========================================"
    echo "ERREUR lors de l'exécution des tests"
    echo "========================================"
    exit 1
fi

