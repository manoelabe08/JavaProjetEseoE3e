# 📋 STRMS — Simple Task & Resource Management System

> Application Java de gestion de tâches avec contrôle d'accès basé sur les rôles (RBAC), gestion des dépendances entre tâches et interface graphique Swing.

***

## 📚 Table des matières

- [Présentation](#-présentation)
- [Fonctionnalités](#-fonctionnalités)
- [Architecture](#-architecture)
- [Structure du projet](#-structure-du-projet)
- [Rôles et permissions](#-rôles-et-permissions)
- [Cycle de vie d'une tâche](#-cycle-de-vie-dune-tâche)
- [Prérequis & Installation](#-prérequis--installation)
- [Lancer l'application](#-lancer-lapplication)
- [Tests](#-tests)
- [Persistance des données](#-persistance-des-données)
- [Exceptions métier](#-exceptions-métier)

***

## 🧭 Présentation

**STRMS** (Simple Task & Resource Management System) est une application de bureau développée en **Java SE** avec une interface graphique **Swing**. Elle permet à des équipes de gérer leurs tâches selon un système de rôles strict : les **Admins** créent et suppriment, les **Managers** assignent et supervisent, et les **Engineers** exécutent.

Le système garantit la cohérence des données grâce à :
- un **graphe de dépendances** entre tâches avec détection des cycles (DFS),
- un **historique complet** de chaque tâche,
- une **persistance fichier** (`.txt`) pour les tâches, les utilisateurs et les historiques.

***

## ✨ Fonctionnalités

- ✅ Création, assignation, démarrage et complétion de tâches
- ✅ Gestion des dépendances inter-tâches (blocage automatique)
- ✅ Détection des dépendances circulaires (algorithme DFS)
- ✅ Contrôle d'accès par rôle (RBAC) — Admin, Manager, Engineer
- ✅ Historique d'actions horodaté pour chaque tâche
- ✅ Tri automatique par priorité (PriorityQueue)
- ✅ Authentification par identifiant / mot de passe
- ✅ Persistance des données en fichiers texte (`.txt`)
- ✅ Interface graphique Swing complète
- ✅ Génération de rapports

***

## 🏗️ Architecture

Le projet suit une architecture en couches avec séparation stricte des responsabilités :

```
┌─────────────────────────────────────────────────┐
│               Interface (Swing)                  │
│          STRMSGui.java · LoginDialog.java         │
├─────────────────────────────────────────────────┤
│              Logique métier (Services)           │
│       TaskManager.java · AuthService.java        │
├─────────────────────────────────────────────────┤
│                  Modèle (Domain)                 │
│    Task · User · Engineer · Manager · Admin      │
├─────────────────────────────────────────────────┤
│             Persistance (File I/O)               │
│               FileManager.java                   │
└─────────────────────────────────────────────────┘
```

### Patrons de conception utilisés

| Patron | Où |
|--------|----|
| RBAC (Role-Based Access Control) | Classe abstraite `User` + sous-classes |
| Strategy | Permissions abstraites via méthodes `canXxx()` |
| Observer-like | Historique `TaskHistoryEntry` dans chaque `Task` |
| Comparable | Tri par priorité dans `PriorityQueue` |

***

## 📁 Structure du projet

```
STRMS/
├── src/
│   └── main/java/
│       ├── enums/
│       │   ├── NotificationType.java
│       │   ├── PriorityLevel.java
│       │   ├── TaskCategory.java
│       │   └── TaskStatus.java
│       ├── exceptions/
│       │   ├── CircularDependencyException.java
│       │   ├── DependencyNotCompletedException.java
│       │   ├── DuplicateTaskException.java
│       │   ├── FilePersistenceException.java
│       │   ├── InvalidRoleException.java
│       │   ├── InvalidTaskStateException.java
│       │   └── TaskNotFoundException.java
│       ├── file_management/
│       │   └── FileManager.java
│       ├── task_management/
│       │   ├── Task.java
│       │   ├── TaskHistoryEntry.java
│       │   └── TaskManager.java
│       └── user/
│           ├── Admin.java
│           ├── AuthService.java
│           ├── Engineer.java
│           ├── Manager.java
│           ├── User.java (abstract)
│           └── UserAccount.java
└── src/
    └── test/java/
        ├── task_management/
        │   ├── TaskTest.java
        │   └── TaskManagerTest.java
        ├── user/
        │   └── AuthServiceTest.java
        └── file_management/
            └── FileManagerTest.java
```

***

## 👥 Rôles et permissions

| Permission | Admin | Manager | Engineer |
|---|:---:|:---:|:---:|
| Créer une tâche | ✅ | ❌ | ❌ |
| Supprimer une tâche | ✅ | ❌ | ❌ |
| Assigner une tâche | ✅ | ✅ | ❌ |
| Mettre à jour une tâche | ✅ | ✅ | ✅ |
| Générer un rapport | ✅ | ✅ | ❌ |
| Exécuter / compléter une tâche | ❌ | ❌ | ✅ |

> Un ingénieur ne peut démarrer ou compléter **que les tâches qui lui sont assignées**.

***

## 🔄 Cycle de vie d'une tâche

```
         ┌──────────────────────────────────────┐
         │                                      │
  [Créée] ──► TODO ──► IN_PROGRESS ──► DONE    │
                │                               │
                │  (dépendance non terminée)    │
                ▼                               │
            BLOCKED ──────────────────────────►─┘
           (auto-résolu quand dépendances DONE)
```

- **TODO** : état initial à la création
- **BLOCKED** : une dépendance n'est pas encore `DONE`
- **IN_PROGRESS** : l'ingénieur assigné a démarré la tâche
- **DONE** : état terminal — aucun retour en arrière possible

***

## ⚙️ Prérequis & Installation

### Prérequis

- **Java 17+** (ou Java 11 minimum)
- **Maven 3.8+**

### Cloner le projet

```bash
git clone https://github.com/<your-username>/STRMS.git
cd STRMS
```

### Compiler

```bash
mvn clean compile
```

***

## 🚀 Lancer l'application

```bash
mvn exec:java -Dexec.mainClass="gui.STRMSGui"
```

Ou depuis un IDE (IntelliJ IDEA, Eclipse) : exécuter directement la classe `STRMSGui.java`.

### Connexion par défaut (exemple)

| UID | Mot de passe | Rôle |
|-----|-------------|------|
| `A001` | `admin123` | Admin |
| `M001` | `manager123` | Manager |
| `E001` | `eng123` | Engineer |

> Les comptes sont chargés depuis le fichier `data/users.txt` au démarrage.

***

## 🧪 Tests

Les tests sont écrits avec **JUnit 5** et couvrent les couches métier, authentification et persistance.

```bash
# Lancer tous les tests
mvn test

# Lancer une classe de test spécifique
mvn test -Dtest=TaskManagerTest
```

### Couverture des tests

| Classe testée | Tests |
|---|---|
| `Task` | Statuts, dépendances, historique, comparaison par priorité |
| `TaskManager` | RBAC, dépendances circulaires, cycle de vie complet |
| `AuthService` | Login, changement de mot de passe, persistance fichier |
| `FileManager` | Création, écriture, lecture, erreurs |

***

## 💾 Persistance des données

Toutes les données sont stockées dans des **fichiers texte** dans le répertoire `data/` :

| Fichier | Contenu | Format |
|---------|---------|--------|
| `data/users.txt` | Comptes utilisateurs | `id;name;email;password;role` |
| `data/tasks.txt` | Tâches | `id;title;desc;priority;status;category;deadline;engineerId` |
| `data/histories.txt` | Historiques des tâches | Format structuré avec séparateur `====` |

> Les fichiers et répertoires sont créés automatiquement au premier lancement.

***

## 🚨 Exceptions métier

| Exception | Déclenchée quand |
|-----------|-----------------|
| `InvalidRoleException` | Un utilisateur tente une action non autorisée par son rôle |
| `DuplicateTaskException` | Création d'une tâche avec un ID déjà existant |
| `TaskNotFoundException` | Recherche d'une tâche avec un ID inexistant |
| `CircularDependencyException` | Ajout d'une dépendance créant un cycle |
| `DependencyNotCompletedException` | Démarrage d'une tâche avec des dépendances non terminées |
| `InvalidTaskStateException` | Transition d'état invalide (ex. : retour depuis DONE) |
| `FilePersistenceException` | Erreur de lecture ou d'écriture d'un fichier de données |

***

## 📄 Licence

Ce projet est développé dans un cadre académique — **ESEO**.

***

*Développé avec ☕ Java & Swing*