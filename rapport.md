PAGE DE TITRE

Projet Universitaire - Programmation Orientée Objet (Java)
Smart Task & Resource Management System (STRMS)

Filière : E3e S6 - Printemps 2026
Établissement : ESEO - Innovation Makes Sense
Date de remise : 28 Mai 2026
Évaluation : Soutenance prévue le 4 Juin 2026

Membres de l'équipe : 
ABE MANOEL DAVE MARTIAL
KANKEU STEVEN NOMESSI
AKPAH STARKER
ATTOUMANI AZID
KARAGNARA FATOUMATA

Sous la supervision de : Mme Samira Aboud

\pagebreak

TABLE DES MATIÈRES

Introduction

Analyse des besoins

Conception et architecture du système
3.1. Approche Orientée Objet (Piliers fondamentaux)
3.2. Architecture des paquets (Packages)
3.3. Choix et justification des structures de données

Diagrammes UML et modélisation dynamique
4.1. Modélisation de la création de tâche (Scénario 1)
4.2. Modélisation de l'assignation (Scénario 2)
4.3. Modélisation de l'exécution (Scénario 3)
4.4. Modélisation des dépendances et détection circulaire (Scénario 4)
4.5. Modélisation de l'authentification et de la persistance (Scénario 5)

Implémentation et fonctionnement des modules clés
5.1. Environnement et méthodologie
5.2. Gestion du cycle de vie des tâches
5.3. Algorithme de détection des dépendances circulaires
5.4. Traçabilité et historique
5.5. Interface Utilisateur (GUI)
5.6. Persistance des données (Omissions et choix)

Gestion des exceptions et sécurité du système

Stratégie de tests automatisés (JUnit)

Difficultés rencontrées et solutions apportées

Conclusion

Références

\pagebreak

1. INTRODUCTION

Cette section présente le contexte général du projet, les objectifs fixés par l'équipe pédagogique et la vision globale du système informatique développé.

Dans le cadre du semestre de printemps 2026 (E3e S6) à l'ESEO, la réalisation du projet Smart Task & Resource Management System (STRMS) constitue l'aboutissement de notre apprentissage en Programmation Orientée Objet (POO) avec le langage Java. Ce projet vise à concevoir un logiciel de gestion de tâches simulant un environnement d'ingénierie réel.

L'objectif principal n'est pas seulement de produire un code fonctionnel, mais d'appliquer de manière rigoureuse les piliers fondamentaux de la conception logicielle : l'encapsulation, l'héritage, le polymorphisme et l'abstraction. De plus, ce projet exige la maîtrise de concepts avancés tels que les structures de données (collections), la manipulation de fichiers pour sauvegarder les données, la gestion des erreurs imprévues (exceptions), et la vérification automatisée de notre code (tests JUnit).

Le système STRMS que nous avons développé permet à différents collaborateurs (Administrateurs, Managers et Ingénieurs) de se connecter et d'interagir avec un espace de travail commun. Ils peuvent y créer des tâches, définir des priorités, imposer des ordres d'exécution stricts (dépendances) et tracer la moindre modification grâce à un historique inviolable.

Dans ce rapport, nous détaillerons la démarche d'analyse, les choix architecturaux et techniques opérés par notre équipe, l'implémentation des algorithmes complexes (notamment pour éviter les blocages de tâches), ainsi que la validation de la robustesse de l'application. Pour faciliter la lecture, les concepts techniques seront régulièrement accompagnés d'analogies issues de la vie courante.

2. ANALYSE DES BESOINS

Cette section traduit les exigences brutes du cahier des charges en besoins concrets et compréhensibles, définissant ce que le logiciel doit faire sans entrer dans le "comment".

Le cahier des charges impose la création d'un système robuste, capable de modéliser les contraintes réelles de la gestion de projet. Nous avons identifié trois grands axes de besoins.

2.1. Les rôles et les permissions (Contrôle d'accès)

Le logiciel doit être sécurisé et restreindre les actions en fonction du rôle de la personne connectée. Pour comprendre, imaginons un chantier de construction :

L'Administrateur (Admin) : C'est l'architecte en chef. Il a tous les droits, notamment celui de créer les tâches, de les supprimer et de structurer le projet. Cependant, il ne "construit" pas lui-même.

Le Manager : C'est le chef de chantier. Il ne crée pas de nouvelles missions, mais il assigne le travail disponible aux ouvriers, surveille l'avancement et génère des rapports globaux.

L'Ingénieur (Engineer) : C'est l'ouvrier spécialisé. Il est le seul autorisé à travailler concrètement sur les tâches (les démarrer, les terminer). Il ne voit et ne manipule que ce qui lui est assigné.

2.2. La logique des tâches et des dépendances

Une tâche est une unité de travail définie par une catégorie (Correction de bug, Nouvelle fonctionnalité, etc.), une urgence (Priorité), une date limite et un statut d'avancement.
La contrainte la plus forte du projet est la gestion des dépendances. Dans la réalité, on ne peut pas construire le toit d'une maison si les murs ne sont pas terminés. De la même manière, le système doit garantir qu'une tâche B ne peut pas être commencée si la tâche A, dont elle dépend, n'est pas totalement achevée.

Une exigence absolue du cahier des charges est la prévention des dépendances circulaires. Si la tâche A dépend de B, que B dépend de C, et que l'utilisateur tente de dire que C dépend de A, le système se bloquerait à l'infini (personne ne pourrait commencer). Le logiciel doit détecter ce piège avant même qu'il ne se referme et l'interdire.

2.3. Historique et persistance

Chaque action effectuée sur une tâche doit laisser une trace indélébile (Qui a fait quoi, et quand ?). C'est le principe de l'historique de tâche. Enfin, comme toute application réelle, les données ne doivent pas disparaître lorsque l'on ferme le programme : le système doit pouvoir enregistrer et recharger les utilisateurs et les tâches depuis des fichiers sur le disque dur.

3. CONCEPTION ET ARCHITECTURE DU SYSTÈME

Cette section explique comment nous avons pensé l'intérieur du logiciel. Nous détaillons la façon dont le code est rangé et les principes théoriques qui gouvernent nos choix.

L'architecture de STRMS repose sur la séparation des responsabilités. Le code a été découpé en plusieurs dossiers ("packages") ayant chacun un rôle bien précis, rappelant le fonctionnement d'une entreprise où chaque département a sa spécialité.

3.1. Approche Orientée Objet (Piliers fondamentaux)

L'entièreté de notre code respecte les quatre piliers de la Programmation Orientée Objet (POO) :

L'Encapsulation : Ce concept consiste à cacher le fonctionnement interne des objets. Dans notre classe Task, toutes les données sensibles (comme le status ou la priority) sont privées. Un utilisateur ne peut pas les modifier directement de force ; il doit utiliser une méthode de sécurité (comme updateStatus) qui vérifiera d'abord si le changement est autorisé par les règles de l'entreprise.

L'Abstraction : Nous avons créé une classe User abstraite. Cela signifie qu'un "Utilisateur" générique n'existe pas dans notre système : on est forcément un Admin, un Manager ou un Engineer. La classe User sert de moule qui force tous ces profils à posséder un nom, un identifiant, et à définir leurs permissions.

L'Héritage : Les classes Admin, Manager et Engineer "héritent" de la classe User. Elles récupèrent automatiquement toutes ses caractéristiques, ce qui nous évite de dupliquer le code, tout en permettant à chacune d'avoir ses spécificités.

Le Polymorphisme : C'est la capacité du système à traiter différents objets de la même manière. Par exemple, la méthode abstraite canCreateTask() est présente chez tous les utilisateurs. Si le système demande à un objet "Peux-tu créer une tâche ?", l'objet répondra true si c'est un Admin, et false si c'est un Engineer, sans que le système n'ait besoin de savoir à l'avance à qui il s'adresse.

3.2. Architecture des paquets (Packages)

enums : Contient les listes de valeurs fixes (Statuts de tâches, Niveaux de priorité). C'est le vocabulaire strict du programme.

user : Gère les profils humains, leurs droits d'accès, et le service d'authentification (AuthService).

task_management : C'est le "cerveau" métier. On y trouve la classe Task, l'historique TaskHistoryEntry, et le contrôleur central TaskManager qui fait appliquer les règles.

exceptions : Contient les alertes personnalisées du système (ex: InvalidRoleException).

file_management : L'accès au disque dur pour lire et écrire les fichiers.

gui : L'Interface Utilisateur Graphique (fenêtres, boutons, tableaux).

utility : Des outils complémentaires comme le tableau de bord (Dashboard) et le générateur de rapports (ReportGenerator).

3.3. Choix et justification des structures de données

Le cahier des charges exigeait une utilisation intelligente des structures de données Java. Ces structures sont des manières différentes d'organiser l'information en mémoire.

HashMap (Pour les tâches et les utilisateurs) : * Concept : Un dictionnaire où chaque mot donne accès à une définition immédiate.

Utilisation : Dans TaskManager, les tâches sont rangées dans une HashMap<String, Task> (L'identifiant "T1" donne accès à l'objet Tâche).

Justification : Cela permet de retrouver une tâche instantanément (complexité O(1)) parmi des milliers, sans avoir à parcourir toute la liste une par une.

HashSet (Pour les tâches en cours) :

Concept : Un club privé très strict où personne ne peut entrer deux fois.

Utilisation : La variable inProgressTasks stocke les tâches actuellement travaillées.

Justification : Garantit l'unicité. Une tâche ne peut pas être démarrée deux fois en même temps. La vérification de présence y est également instantanée.

PriorityQueue (Pour l'ordre d'exécution) :

Concept : La salle d'attente des urgences d'un hôpital. Ce n'est pas le premier arrivé qui passe, mais le cas le plus grave.

Utilisation : La variable taskQueue.

Justification : Bien que la priorité doive respecter les dépendances, cette structure pré-trie automatiquement les tâches grâce à la méthode compareTo de la classe Task (qui lit l'urgence CRITICAL, HIGH, etc.).

ArrayList (Pour l'historique) :

Concept : Un journal de bord chronologique.

Utilisation : La variable history dans la classe Task.

Justification : Conserve l'ordre exact d'insertion des événements, parfait pour tracer la vie d'une tâche de sa création à sa clôture.

4. DIAGRAMMES UML ET MODÉLISATION DYNAMIQUE

Cette section interprète les diagrammes de séquence fournis par l'équipe, qui illustrent comment les composants du code discutent entre eux au cours du temps.

Pour s'assurer que notre conception était solide avant même de coder, nous avons produit plusieurs diagrammes de séquence. Ils se lisent de haut en bas (chronologie) et de gauche à droite (interactions).

4.1. Modélisation de la création de tâche (Scénario 1)

Lorsqu'un Administrateur (Alice) clique sur "Créer tâche" dans l'interface (STRMSGui), la requête est envoyée au TaskManager. Le cerveau du système effectue alors une vérification vitale : il demande à l'objet User "As-tu la permission ?" (canCreateTask()). Si c'est un Ingénieur qui avait essayé de forcer l'interface, une exception InvalidRoleException aurait été levée. Alice ayant le droit, la tâche est instanciée, stockée dans la HashMap et la PriorityQueue, et un premier événement est inscrit dans son historique inviolable.

4.2. Modélisation de l'assignation (Scénario 2)

Le Manager (Bob) choisit une tâche et un Ingénieur. Le TaskManager vérifie d'abord que la tâche existe. Ensuite, la permission de Bob est vérifiée (canAssignTask()). Si tout est correct, le système indique à la Task qu'elle appartient à l'Ingénieur, et la tâche est ajoutée à la charge de travail de cet Ingénieur. Le détail crucial ici est l'appel à refreshBlockedStatus() : le système recalcule automatiquement si la tâche peut passer à TODO ou si elle doit rester bloquée (BLOCKED) à cause de ses dépendances.

4.3. Modélisation de l'exécution (Scénario 3)

L'Ingénieur (Charlie) veut démarrer sa tâche. C'est ici que les règles métier sont les plus strictes. Le TaskManager invoque hasUnfinishedDependencies(). Si des tâches requises ne sont pas terminées, l'action est violemment rejetée avec une DependencyNotCompletedException et l'interface affiche une erreur. Dans le cas contraire, le statut passe à IN_PROGRESS et la tâche rejoint le fameux club très privé des tâches en cours (HashSet). Lors de la complétion, une sécurité supplémentaire (InvalidTaskStateException) empêche de changer le statut d'une tâche déjà déclarée DONE.

4.4. Modélisation des dépendances et détection circulaire (Scénario 4)

C'est le scénario le plus complexe de notre architecture. L'Admin veut que la Tâche A dépende de la Tâche C. Avant d'accepter, le TaskManager s'isole et lance la méthode secrète detectCircularDependency. Si ce radar détecte que C dépend de B, et que B dépend déjà de A, il en conclut que la boucle est bouclée (Cycle détecté). L'opération est immédiatement annulée par une CircularDependencyException. Le diagramme illustre parfaitement que dans ce cas, le graphe des tâches reste strictement inchangé.

4.5. Modélisation de l'authentification et de la persistance (Scénario 5)

L'utilisateur s'identifie sur le LoginDialog. Le service AuthService compare les identifiants avec ceux stockés en mémoire. En cas de succès, la fenêtre principale STRMSGui s'ouvre et s'adapte polymorphiquement au rôle (boutons grisés si non autorisés). Lorsqu'on sauvegarde, le TaskManager compile les données et demande au FileManager d'écrire sur le disque. Une panne matérielle (disque plein, fichier introuvable) lève une FilePersistenceException gérée proprement pour ne pas faire crasher l'application.

5. IMPLÉMENTATION ET FONCTIONNEMENT DES MODULES CLÉS

Cette section décrit comment la théorie est devenue réalité dans notre code source, et analyse les mécanismes algorithmiques que nous avons développés.

5.1. Environnement et méthodologie

Le développement s'est déroulé dans l'Environnement de Développement Intégré (IDE) Eclipse. Nous avons utilisé un gestionnaire de version (Git) pour répartir le travail, centraliser le code et éviter d'écraser le travail de nos camarades. Les conventions de nommage Java (CamelCase, classes en majuscules) ont été rigoureusement respectées.

5.2. Gestion du cycle de vie des tâches

Le cycle de vie est géré par l'énumération TaskStatus (TODO, BLOCKED, IN_PROGRESS, DONE).
La méthode updateStatus dans Task.java agit comme un vigile intraitable. Par exemple, si une tâche est DONE, une condition vérifie : if (status == TaskStatus.DONE && newStatus != TaskStatus.DONE). Si un utilisateur essaie de la repasser en IN_PROGRESS, le vigile lève une exception. C'est la matérialisation parfaite des règles imposées par le professeur dans le cahier des charges : un état terminal est définitif.

5.3. Algorithme de détection des dépendances circulaires

C'est la pièce maîtresse technique du TaskManager. Pour résoudre ce problème, nous avons implémenté un algorithme de recherche en profondeur (DFS - Depth-First Search).
Concrètement, la méthode récursive detectCircularDependency(Task current, Task target) fonctionne ainsi :

Elle regarde si la tâche qu'on veut ajouter (target) est déjà dans la liste de dépendances directes de la tâche current.

Si non, elle inspecte les dépendances des dépendances en s'appelant elle-même (récursivité).

Si, au fil de sa recherche, elle retombe sur la tâche cible, c'est qu'il existe un chemin qui boucle. Elle renvoie true (danger).
Cette méthode protège le système des blocages dits de "Deadlock" (impasse logique).

5.4. Traçabilité et historique

La traçabilité est assurée par la classe TaskHistoryEntry. C'est une classe délibérément "immuable" (ses variables sont définies avec le mot-clé final et n'ont aucun "setter"). Une fois créées lors d'une action, la date, l'auteur et la description ne peuvent plus jamais être modifiés, même par erreur. Le TaskManager se charge d'ajouter une entrée à la liste history d'une tâche à chaque changement d'état, assignation, ou création.

5.5. Interface Utilisateur (GUI)

L'interface a été conçue avec la bibliothèque Java Swing (STRMSGui.java). Elle utilise un système d'onglets (JTabbedPane) pour organiser les espaces de travail. Une fonctionnalité majeure de notre implémentation est la méthode applyPermissions() : en lisant les réponses de l'objet utilisateur connecté (ex: currentUser.canAssignTask()), elle active ou désactive visuellement les onglets complets. Ainsi, un Ingénieur ne verra même pas l'interface de création de tâches, renforçant la sécurité globale.

5.6. Persistance des données (Omissions et choix)

Le module FileManager utilise des flux (BufferedReader, BufferedWriter) pour transcrire les objets Java en lignes de texte lisibles (format pseudo-CSV séparé par des points-virgules ;).

Les comptes utilisateurs : Ils sont parfaitement sauvegardés et rechargés par AuthService, ce qui maintient les mots de passe et les rôles.

Remarque sur le cahier des charges : Nous avons identifié une omission dans notre implémentation par rapport au cahier des charges (Section 6.1.3). Actuellement, la méthode saveTasksToFile() sauvegarde l'état de base d'une tâche mais ne sauvegarde pas son graphe de dépendances. De plus, lors du chargement, toutes les tâches reviennent artificiellement au statut TODO. Bien que l'architecture des fichiers soit robuste et intercepte bien les erreurs I/O, la complexité de relier des objets "Tâches" entre eux après lecture depuis un fichier plat a constitué une limite de notre implémentation actuelle.

De la même façon, la section 6.1.3 demandait la suppression de dépendance, or les méthodes métier removeDependency n'ont pas encore été implémentées dans nos classes.

6. GESTION DES EXCEPTIONS ET SÉCURITÉ DU SYSTÈME

Les exceptions sont les alarmes et les coupe-circuits de notre système. Lorsqu'une action enfreint les règles de la physique de notre logiciel, une exception interrompt l'action avant que des dégâts ne soient causés.

Nous avons créé 7 exceptions personnalisées, toutes héritant de la classe mère Java Exception, pour qualifier précisément la source du problème :

InvalidRoleException : Déclenchée si un Ingénieur essaie d'assigner une tâche, ou si un Admin tente de travailler sur une tâche. Protège la hiérarchie.

CircularDependencyException : Déclenchée par l'algorithme DFS. Protège l'intégrité du graphe de tâches.

DependencyNotCompletedException : Empêche l'exécution hâtive d'une tâche si ses prérequis ne sont pas atteints (statut DONE).

InvalidTaskStateException : Interdit les voyages dans le temps pour une tâche (ex: repasser en TODO quand on est DONE).

TaskNotFoundException : Levier de sécurité si l'interface demande à modifier une tâche dont l'ID a disparu de la HashMap.

DuplicateTaskException : Empêche d'avoir deux tâches "T1". Protège l'unicité de la clé dans la HashMap.

FilePersistenceException : Enveloppe les erreurs complexes du système d'exploitation (IOException) dans une erreur compréhensible par le programme (ex: Fichier corrompu ou dossier en lecture seule).

7. STRATÉGIE DE TESTS AUTOMATISÉS (JUNIT 5)

Un code qui compile n'est pas un code qui fonctionne. Cette section explique comment nous avons automatisé la validation de notre logique à l'aide de tests unitaires JUnit 5.

Conformément à la section 7 du cahier des charges, nous avons mis en place une suite de tests rigoureuse, située dans le dossier test/java. Notre stratégie repose sur des tests isolés, automatisés et répétables.

Méthodologie globale :
Chaque classe de test utilise les annotations @BeforeEach pour réinitialiser un environnement sain (remise à zéro des objets TaskManager, User, Task) avant chaque méthode @Test. Nous utilisons assertThrows pour vérifier que les coupe-circuits (Exceptions) fonctionnent correctement.

Couverture des scénarios clés :

Validation des permissions (UserRolesTest.java) : Nous testons chaque rôle un par un. Par exemple, nous vérifions via assertTrue qu'un Admin peut générer un rapport, et via assertFalse qu'un Ingénieur ne le peut pas.

Tests des fichiers (FileManagerTest.java, AuthServiceTest.java) : Une excellente pratique que nous avons implémentée est l'utilisation de l'annotation JUnit @TempDir. Cela génère des dossiers virtuels éphémères pour tester l'écriture et la lecture de fichiers sans polluer le disque dur réel de la machine.

Transitions d'états (TaskTest.java) : Nous simulons la résolution d'une dépendance et vérifions que la tâche dépendante passe magiquement de BLOCKED à TODO via la méthode refreshBlockedStatus.

Le test crucial du cahier des charges - L'intégrité du graphe (TaskManagerTest.java) : La directive 7.2 du PDF exigeait de vérifier "le maintien de l'intégrité du graphe après rejet". Dans notre méthode shouldRejectCircularDependency, après nous être assurés que l'exception CircularDependencyException est bien levée, nous ajoutons une vérification (assertion) cruciale : nous certifions que la tâche incriminée ne contient effectivement pas la mauvaise dépendance dans sa liste, prouvant que le système est resté stable et intact après l'incident.

Test manquant : Bien que exigé par le cahier des charges (Section 7.2), nous n'avons pas pu tester le "Retrait correct des dépendances" car, comme évoqué plus haut, nous n'avons pas encore programmé cette fonctionnalité dans la logique métier.

8. DIFFICULTÉS RENCONTRÉES ET SOLUTIONS APPORTÉES

Le développement d'un logiciel complexe ne se fait jamais sans accroc. Voici notre retour d'expérience.

Le casse-tête des dépendances circulaires : Au départ, nous pensions qu'une simple boucle for suffisait pour vérifier si Tâche A dépendait de Tâche B. Mais si la boucle passe par 15 tâches intermédiaires, la vérification simple devenait obsolète.

Solution : En révisant nos cours de structures de données, nous avons compris la pertinence du Graphe. L'implémentation de la fonction DFS récursive (où une méthode s'appelle elle-même en creusant de plus en plus profond) a résolu ce problème de façon très élégante et optimisée.

Synchronisation entre le Backend (métier) et le Frontend (GUI) : Dans Swing, lorsqu'une tâche est mise à jour dans la HashMap par le TaskManager, le tableau visuel (JTable) ne se mettait pas à jour de lui-même.

Solution : Création de la méthode refreshTaskTable() dans STRMSGui, qui est invoquée systématiquement après chaque action réussie pour vider le tableau et le redessiner à partir des données fraîches du modèle.

Persistance des graphes complexes : Sauvegarder des objets simples dans un fichier texte est aisé. Mais sauvegarder des objets qui contiennent des listes pointant vers d'autres objets (les dépendances) s'est révélé être un défi majeur.

Solution partielle : Nous avons réussi à persister le texte de l'historique et les caractéristiques de base. La résolution de la persistance des relations (peut-être en sauvegardant uniquement les ID de dépendances pour les reconnecter post-chargement) restera notre priorité pour les versions futures.

9. CONCLUSION

Bilan de ce projet pédagogique et professionnel.

Le projet STRMS a été un défi formateur exceptionnel. Il nous a poussés bien au-delà de la simple écriture de code : il a fallu concevoir une architecture, penser aux limites du système, anticiper les erreurs humaines, et garantir des règles métiers strictes exigées par un cahier des charges professionnel.

L'application respecte ses promesses majeures : un espace collaboratif sécurisé par rôle, un ordonnancement intelligent des tâches (priorité + File d'attente), et surtout, un moteur de règles robuste capable d'empêcher les dépendances circulaires, assurant qu'aucun projet ne puisse entrer dans un blocage définitif (deadlock). La traçabilité inviolable apporte une dimension "audit" digne des logiciels d'entreprise (Jira, Trello).

Les quelques omissions résiduelles (sauvegarde complète du graphe, suppression de dépendance) constituent notre feuille de route pour de futures améliorations. Ce projet nous a permis d'assimiler profondément la puissance des piliers de la Programmation Orientée Objet. L'encapsulation et le polymorphisme, couplés à une gestion minutieuse des structures de données Java, ont permis de rendre lisible, maintenable et évolutif un système dont la complexité sous-jacente est immense.

10. RÉFÉRENCES

Cahier des charges officiel : "Java Object-Oriented Programming Project - Smart Task & Resource Management System (STRMS)" fourni par l'équipe pédagogique (E3e S6 Spring 2026).

Documentation officielle d'Oracle : Java Platform, Standard Edition & Java Development Kit Version 21 API Specification (pour l'usage des HashMap, PriorityQueue, flux I/O).

Documentation JUnit 5 (org.junit.jupiter.api) : Pour l'élaboration des suites de tests unitaires et l'utilisation de l'annotation @TempDir.

Principes de l'algorithmique des graphes : Recherche en profondeur (DFS) appliquée à la détection de cycles.