# Gestion de Stock — frontend Angular

Interface de la plateforme de gestion de stock : produits, catégories,
fournisseurs, mouvements, clients, commandes et paiements.

Angular 20, composants autonomes, routes chargées à la demande.
Le backend correspondant vit dans le dépôt
[`gestion-de-stock-v2/backend`](https://github.com/gestion-de-stock-v2/backend).

---

## Démarrage

```bash
npm ci
npm start          # http://localhost:4200
```

Le serveur de développement relaie `/api` vers la passerelle sur `:8222`
(voir [`proxy.conf.json`](proxy.conf.json)). Le backend doit donc tourner :

```bash
# depuis le dépôt backend, cloné à côté
cd ../backend && cp .env.example .env && docker compose up -d
```

> `proxy.conf.json` n'est lu qu'au **démarrage** : après l'avoir modifié,
> relancez `npm start`.

### Comptes de démonstration

Disponibles quand le backend tourne avec `SPRING_PROFILES_ACTIVE=dev` :
`admin` / `admin123!` (et un compte par rôle, voir le README du backend).

---

## Charte graphique

[`DESIGN.md`](DESIGN.md) est la **source unique de vérité** : palette,
typographie, rayons, élévations, espacements.

`src/styles.css` traduit cette charte en tokens CSS. Toute couleur, tout rayon
et toute ombre doit provenir d'un token — jamais d'une valeur en dur dans une
feuille de composant.

### Spectre des rôles

Les sept rôles métier disposent de teintes dédiées, documentées dans
`DESIGN.md § Role Spectrum`. Deux propriétés sont garanties et **vérifiables** :

```bash
python3 tools/check-palette.py
```

Le script recalcule les contrastes (seuil WCAG AA de 4.5:1 sur le fond
réellement utilisé par la pastille), mesure la séparation des teintes sur la
roue chromatique, et contrôle que `src/styles.css` déclare exactement les
valeurs de `DESIGN.md`. Ce dernier point évite la dérive silencieuse entre la
charte et son implémentation.

À exécuter après toute modification du spectre.

---

## Structure

```
src/app/
├── components/      icônes, bascule de thème
├── guards/          authentification et rôles
├── models/          contrats alignés sur les DTO du backend
│   └── role-style.ts  couleurs et icônes des rôles, définies une seule fois
├── pages/           une page par route
└── services/        accès HTTP, thème, authentification
public/fonts/        Plus Jakarta Sans, hébergée localement
```

La police est **servie par l'application** et non depuis un CDN : Angular
inline les feuilles externes au moment du build, ce qui faisait dépendre chaque
compilation d'un appel réseau. Aucune requête tierce à l'exécution.

---

## Commandes

```bash
npm start                              # serveur de développement
npm run build                          # build de développement
npx ng build --configuration production   # build de production
npm test                               # tests unitaires
python3 tools/check-palette.py         # conformité du spectre des rôles
```

---

## Conteneur

```bash
docker build -t gestion-stock-frontend .
docker run -p 4200:80 gestion-stock-frontend
```

L'image sert les fichiers statiques par nginx et relaie `/api` vers un hôte
nommé `gateway` sur le réseau Docker (voir [`nginx.conf`](nginx.conf)). Le
`docker-compose.yml` du dépôt backend orchestre l'ensemble.

---

## Points connus

- **Pas de tests de composants.** Les fichiers `.spec.ts` présents sont les
  stubs générés par Angular CLI ; la conformité de la charte est vérifiée par
  `tools/check-palette.py`, pas le rendu des pages.
- Les répertoires `pages/produtos/`, `pages/fornecedores/` et `pages/usuarios/`
  conservent leur nom d'origine ; seuls le code et les contrats d'API ont été
  harmonisés en anglais.
