-- Création de la base ChriOnline (à exécuter en tant que superutilisateur, ex: postgres)
-- Usage : psql -U postgres -f create_database.sql

SELECT 'CREATE DATABASE chrionline'
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'chrionline')\gexec
