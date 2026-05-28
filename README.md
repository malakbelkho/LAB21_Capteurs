# Sensor Aura – LAB 21

Application Android permettant d’explorer les capteurs embarqués d’un smartphone en temps réel à travers une interface moderne, colorée et interactive.

## Objectif:

Le but de ce laboratoire est de :
- Comprendre le rôle de `SensorManager` dans Android
- Lister les capteurs disponibles sur un téléphone ou un émulateur
- Lire les caractéristiques techniques de chaque capteur
- Exploiter `SensorEventListener` pour recevoir des mesures en temps réel
- Afficher l’évolution des mesures sous forme de graphe
- Utiliser plusieurs capteurs embarqués :
  - Accéléromètre
  - Gyroscope
  - Champ magnétique
  - Gravité
  - Proximité
  - Température
  - Humidité
  - Compteur de pas
- Créer une boussole numérique
- Mettre en place une reconnaissance simple d’activité
- Tester l’application sur émulateur et sur téléphone réel

## Description de l’application:

L’application **Sensor Aura** permet de visualiser les capteurs Android disponibles et d’interagir avec eux depuis une interface personnalisée.

Elle contient :
- Un catalogue complet des capteurs disponibles
- Des cartes détaillées pour chaque capteur
- Une navigation horizontale entre les modules
- Des graphes dynamiques pour suivre les mesures
- Une boussole numérique
- Un compteur de pas
- Une reconnaissance simple d’activité
- Un mode simulation pour les capteurs absents ou instables sur l’émulateur

## Fonctionnalités:

- Affichage dynamique de la liste des capteurs disponibles
- Affichage des informations techniques :
  - Nom du capteur
  - Fabricant
  - Version
  - Type Android
  - Type numérique
  - Résolution
  - Consommation énergétique
  - Portée maximale
  - Délai minimal d’acquisition
- Lecture en temps réel des valeurs des capteurs
- Visualisation graphique des mesures
- Simulation automatique sur émulateur pour certains capteurs
- Lecture de l’accéléromètre selon les axes X, Y et Z
- Lecture du gyroscope
- Lecture du champ magnétique
- Lecture de la gravité
- Détection de proximité
- Compteur de pas avec permission Android
- Boussole basée sur l’accéléromètre et le magnétomètre
- Reconnaissance simple d’activité :
  - Position stable
  - Téléphone à plat
  - Téléphone vertical
  - Marche
  - Mouvement brusque

## Technologies utilisées:

- Android Studio
- Java
- XML
- Fragments Android
- `SensorManager`
- `SensorEventListener`
- `Canvas`
- `Paint`
- `Path`
- Custom View
- API minimum : 24

## Aperçu de l’application:

▶️ Deux démonstrations vidéo sont disponibles dans le dossier **Demo** du repository :

- Démonstration sur émulateur Android
- Démonstration sur téléphone réel

https://github.com/user-attachments/assets/fc559c22-2fb3-4659-b987-7d6e497fea3b

https://github.com/user-attachments/assets/73774d40-dd04-411c-844a-f42d2946c6cd

⚠️ En cas de problème de lecture depuis le repository :

👉 [▶️ Voir les démos sur Google Drive](https://)

## Captures de l’application:

### Catalogue des capteurs

L’écran catalogue affiche les capteurs disponibles sous forme de cartes modernes avec badges colorés.

```text
Capteurs affichés :
- Accéléromètre
- Gyroscope
- Champ magnétique
- Température
- Humidité
- Proximité
- Gravité
- Rotation vector
- Linear acceleration
```
### Graphes des mesures

Les capteurs comme la température, l’humidité, la proximité et le champ magnétique sont affichés avec un graphe dynamique.

Chaque nouvelle valeur reçue est ajoutée au graphe afin de suivre l’évolution du signal.

### Boussole

La boussole utilise :
- L’accéléromètre
- Le magnétomètre

Elle affiche :
- L’angle en degrés
- La direction correspondante : Nord, Est, Sud, Ouest, etc.

### Reconnaissance d’activité

L’application analyse les valeurs de l’accéléromètre pour estimer l’état de mouvement du téléphone.

Exemples :
- Stable
- Téléphone à plat
- Téléphone vertical
- Marche
- Mouvement brusque

## Structure du projet:

```text
app/src/main/java/com/malak/capteurs/
│
├── MainActivity.java
│
├── fragments/
│   ├── SensorCatalogFragment.java
│   ├── LiveSignalFragment.java
│   ├── MotionAxesFragment.java
│   ├── StepPulseFragment.java
│   ├── CompassAuraFragment.java
│   └── ActivityMoodFragment.java
│
├── utils/
│   └── SensorInfoStyler.java
│
└── views/
    └── AuraLineGraphView.java
```

## Structure des fichiers:

### MainActivity.java

La classe principale de l’application.

Elle permet de :
- Charger l’interface principale
- Gérer la navigation entre les fragments
- Ouvrir le catalogue des capteurs au lancement
- Relier chaque bouton à son écran correspondant

Les boutons disponibles sont :
- Capteurs
- Température
- Humidité
- Proximité
- Champ magnétique
- Accéléromètre
- Gravité
- Gyroscope
- Pas
- Boussole
- Activité

### fragments/SensorCatalogFragment.java

Ce fragment affiche la liste des capteurs disponibles sur le dispositif.

Il utilise :

```java
SensorManager.getSensorList(Sensor.TYPE_ALL)
```

Chaque capteur est présenté dans une carte contenant :
- Le nom
- Le fabricant
- Le type Android
- Le type numérique
- La résolution
- L’énergie consommée
- La portée maximale
- Le délai minimal

Les capteurs non calibrés peuvent être ignorés afin de garder une liste plus claire.

### fragments/LiveSignalFragment.java

Fragment générique utilisé pour afficher les capteurs simples.

Il est utilisé pour :
- Température
- Humidité
- Proximité
- Champ magnétique

Il reçoit trois paramètres :
- Le type du capteur
- Le titre de l’écran
- Le mode de lecture

Pour le champ magnétique, les trois axes X, Y et Z sont combinés avec la formule :

```text
sqrt(x² + y² + z²)
```

Cela permet d’afficher une seule courbe représentant l’intensité globale du champ magnétique.

### fragments/MotionAxesFragment.java

Ce fragment permet de lire les capteurs qui retournent trois valeurs.

Il est utilisé pour :
- Accéléromètre
- Gravité
- Gyroscope

Il affiche :
- Axe X
- Axe Y
- Axe Z
- Norme globale

La norme globale est calculée avec :

```text
sqrt(x² + y² + z²)
```

### fragments/StepPulseFragment.java

Ce fragment gère le compteur de pas.

Il utilise :
- `TYPE_STEP_COUNTER`
- `TYPE_STEP_DETECTOR` comme alternative si disponible

Il affiche :
- Le nombre de pas depuis le dernier redémarrage
- Le nombre de pas pendant la session actuelle

Une permission Android est nécessaire :

```xml
<uses-permission android:name="android.permission.ACTIVITY_RECOGNITION" />
```

Sur émulateur, ce capteur peut être indisponible. Le test est donc plus pertinent sur un téléphone réel.

### fragments/CompassAuraFragment.java

Ce fragment crée une boussole numérique.

Il utilise :
- Accéléromètre
- Magnétomètre

Le calcul repose sur :
- `SensorManager.getRotationMatrix()`
- `SensorManager.getOrientation()`

L’application convertit ensuite l’angle obtenu en degrés et affiche la direction correspondante :
- Nord
- Nord-Est
- Est
- Sud-Est
- Sud
- Sud-Ouest
- Ouest
- Nord-Ouest

### fragments/ActivityMoodFragment.java

Ce fragment réalise une reconnaissance simple d’activité à partir de l’accéléromètre.

L’accéléromètre contient :
- La gravité
- Le mouvement réel du téléphone

Pour isoler le mouvement, l’application applique un filtre simple afin d’estimer la gravité, puis retire cette composante du signal.

La formule utilisée est :

```text
gravity = ALPHA * gravity + (1 - ALPHA) * acceleration
```

Ensuite, l’intensité du mouvement est calculée avec :

```text
sqrt(linearX² + linearY² + linearZ²)
```

L’application utilise une fenêtre de mesures pour éviter les décisions trop rapides.

Les états détectés sont :
- Calibration
- Position stable
- Téléphone à plat
- Téléphone vertical
- Marche
- Mouvement brusque

### utils/SensorInfoStyler.java

Classe utilitaire utilisée pour formater les informations techniques d’un capteur.

Elle transforme un objet `Sensor` en texte lisible pour l’utilisateur.

Informations affichées :
- Nom
- Fabricant
- Version
- Type Android
- Type numérique
- Résolution
- Énergie
- Portée maximale
- Délai minimal

### views/AuraLineGraphView.java

Vue personnalisée utilisée pour dessiner les graphes.

Elle utilise :
- `Canvas`
- `Paint`
- `Path`
- `LinearGradient`

Elle permet de :
- Stocker les dernières valeurs reçues
- Dessiner une courbe dynamique
- Afficher une grille douce
- Afficher les valeurs minimales et maximales
- Créer un rendu visuel moderne sans bibliothèque externe

## Design de l’application:

L’interface a été personnalisée avec :
- Fond en dégradé doux
- Cartes arrondies
- Badges colorés
- Boutons de navigation horizontaux
- Graphes personnalisés
- Couleurs modernes
- Espacement confortable
- Interface responsive

### Fichiers drawable utilisés:

```text
res/drawable/bg_aura_screen.xml
res/drawable/bg_aura_card.xml
res/drawable/bg_sensor_badge.xml
```

### Palette visuelle:

```text
Rose doux
Violet
Bleu clair
Blanc
Gris foncé
Gris secondaire
```

## Tests réalisés:

### Test sur émulateur Android

L’application a été testée sur l’émulateur Android Studio.

Les capteurs virtuels ont permis de tester :
- Catalogue des capteurs
- Température simulée
- Humidité simulée
- Proximité simulée
- Champ magnétique
- Accéléromètre
- Gyroscope
- Gravité
- Boussole

Certains capteurs de l’émulateur retournent des valeurs fixes ou non réalistes. Pour cette raison, l’application active un mode simulation pour certains modules.

### Test sur téléphone réel

L’application a également été testée sur un smartphone Android réel.

Le téléphone réel permet de mieux tester :
- Accéléromètre
- Gyroscope
- Champ magnétique
- Boussole
- Proximité
- Reconnaissance d’activité
- Compteur de pas si le capteur est disponible

Certains téléphones ne possèdent pas de capteur d’humidité ou de température ambiante. Dans ce cas, l’application affiche une simulation ou un message d’indisponibilité.

## Remarques importantes:

- Les capteurs disponibles varient selon le téléphone utilisé.
- L’émulateur peut afficher des capteurs virtuels qui ne fournissent pas toujours des mesures réalistes.
- Le compteur de pas dépend du support matériel du téléphone.
- La température et l’humidité ambiantes sont rarement disponibles sur les smartphones modernes.
- La reconnaissance d’activité reste pédagogique et repose sur des seuils simples.
- Pour une application professionnelle, il serait préférable d’utiliser un modèle d’apprentissage automatique entraîné sur des données réelles.

## Résultat final:

L’application finale permet de :
- Explorer les capteurs disponibles
- Lire leurs caractéristiques techniques
- Afficher les mesures en temps réel
- Visualiser les signaux sous forme de graphe
- Tester les capteurs sur émulateur et téléphone réel
- Utiliser une boussole numérique
- Compter les pas lorsque le capteur est disponible
- Reconnaître simplement certains états de mouvement

## Conclusion:

Ce laboratoire permet de comprendre comment une application Android peut exploiter les capteurs embarqués d’un smartphone.

Il met en pratique :
- La gestion des capteurs avec `SensorManager`
- La réception de données en temps réel avec `SensorEventListener`
- L’organisation du code avec des fragments
- La création d’une vue graphique personnalisée
- La gestion des capteurs absents ou simulés
- L’analyse simple du mouvement

Ce projet constitue une base solide pour développer des applications mobiles liées à la santé, au sport, à l’orientation, à la réalité augmentée ou à l’analyse de mouvement.
