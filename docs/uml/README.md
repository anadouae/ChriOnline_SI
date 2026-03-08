# Modélisation UML - ChriOnline

Diagrammes PlantUML conformes au cahier des charges.

## Fichiers

| Fichier | Description |
|---------|-------------|
| `diagramme_classes_principales.puml` | **Classes principales uniquement** (Utilisateur, Produit, Panier, Commande, etc.) |
| `diagramme_classes_vertical.puml` | **Version verticale** du diagramme complet (organisation top→bottom) |
| `diagramme_classes.puml` | Diagramme de classes complet (obligatoire cahier des charges) |
| `diagramme_cas_utilisation.puml` | Cas d'utilisation (Client, Admin) |
| `diagramme_sequence_achat.puml` | Séquence du processus d'achat |
| `diagramme_composants.puml` | Architecture client-serveur |

## Génération des images

### En ligne
- [PlantUML Online](https://www.plantuml.com/plantuml/uml/)
- Copier-coller le contenu d'un fichier `.puml` → génère le PNG/SVG

### En local (Java)
```bash
# Télécharger plantuml.jar
java -jar plantuml.jar diagramme_classes.puml
# Génère diagramme_classes.png
```

### Extension VS Code
- Installer "PlantUML" (jebbs.plantuml)
- Ctrl+Alt+P pour prévisualiser
