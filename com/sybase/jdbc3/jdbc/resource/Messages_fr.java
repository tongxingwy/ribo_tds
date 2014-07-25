/*     */ package com.sybase.jdbc3.jdbc.resource;
/*     */ 
/*     */ public class Messages_fr extends Messages
/*     */ {
/*  28 */   private static final Object[][] CONTENTS = { { "JZ001", "La propriété user name '%1s' est trop longue. Taille maximale admise : 30." }, { "JZ002", "La propriété password '%1s' est trop longue. Taille maximale admise : 30." }, { "JZ0NE", "Le format de l'URL %1s est incorrect. Message d'erreur : %2s" }, { "JZ003", "Le format de l'URL %1s est incorrect" }, { "JZ0PN", "Le numéro de port spécifié pour %1s est hors intervalle. Les numéros de port doivent satisfaire les conditions suivantes : 0 <= numéroPort <= 65535" }, { "JZ0BC", " [extra arg%1s : %2s]\n" }, { "JZ0BM", " \nErreur interne également détectée : délimiteur d'argument introuvable dans le message d'erreur.  \n Bien que ceci n'affecte en rien le bon déroulement de l'application, contactez le Support Technique de Sybase.\n" }, { "JZ004", "La propriété user name est absente de DriverManager.getConnection(..., Properties)." }, { "JZ006", "IOException détectée : %1s" }, { "JZ0NC", "wasNull appelé pour la première fois pour l'accès à une colonne." }, { "JZ008", "La valeur d'index de colonne %1s est incorrecte." }, { "JZ00F", "La propriété cursor ne peut pas être modifiée après la déclaration du curseur." }, { "JZ00E", "Impossible d'exécuter une instruction de curseur sur le curseur ouvert.  Fermez d'abord l'instruction." }, { "JZ0LC", "Impossible d'appeler la méthode %1s sur un ResultSet qui utilise un curseur de langue pour extraire les lignes.Essayez de régler la propriété de connexion de LANGUAGE_CURSOR sur faux." }, { "JZ00G", "Aucune valeur de colonne n'a été définie pour la mise à jour de cette ligne." }, { "JZ00H", "Le jeu de résultats ne peut pas être modifié.  Supprimez la clause 'FOR READ ONLY' de la requête" }, { "JZ0RM", "La méthode %1 ne peut pas être appelée après updateRow ou deleteRow." }, { "JZ0RD", "Impossible d'appeler les méthodes de ResultSet.get* pour une ligne qui a été effacée par la méthode deleteRow()." }, { "JZ0IR", "getXXX ne peut pas être appelé sur une colonne après mise à jour dans le jeu de résultats à l'aide d'un java.io.Reader." }, { "JZ0IS", "Impossible d'appeler getXXXStream pour une colonne modifiée dans le jeu de résultats." }, { "JZ0BD", "Valeur hors intervalle ou non admise utilisée pour le paramètre de méthode." }, { "JZ0BR", "Le curseur ne se trouve pas sur une ligne qui supporte la méthode %1s." }, { "JZ0BT", "La méthode %1s n'est pas supportée pour les ResultSets de type %2s." }, { "010RC", "La concurrence d'accès et le type de ResultSet demandés ne sont pas supportés. Ils ont été convertis." }, { "000D3", "Votre licence Sybase JDBC va expirer le %1s. Veuillez acquérir une nouvelle licence." }, { "JZ0D4", "Protocole non reconnu dans l'URL JDBC de Sybase : %1s." }, { "JZ0D5", "Erreur lors du chargement du protocole %1s." }, { "JZ0D6", "Le numéro de version, %1s, indiqué dans setVersion n'est pas reconnu. Sélectionnez l'une des valeurs SybDriver.VERSION_* et assurez-vous que la version de jConnect que vous utilisez a bien le niveau de version spécifié ou un niveau supérieur." }, { "JZ0HC", "Un caractère incorrect, '%1s', a été détecté pendant l'analyse de l'hexadécimal." }, { "JZ009", "L'erreur suivante a été détectée pendant la conversion : %1s" }, { "JZ00A", "La précision et l'échelle spécifiées pour la valeur numérique sont incorrectes." }, { "JZ00C", "La précision et l'échelle spécifiées ne sont pas compatibles avec la valeur numérique '%1s'." }, { "JZ00I", "Echelle non valide. L'échelle précisée doit être >= 0." }, { "JZ0TE", "Tentative de conversion non admise entre deux types. Les types de données admis pour la base sont : '%1s'" }, { "JZ0TC", "Tentative de conversion non admise entre deux types." }, { "JZ0TI", "jConnect ne peut pas réaliser de conversion significative entre le type de base de données de '%1s' et le type demandé de '%2s'." }, { "JZ0I3", "Argument incorrect %1s transmis à la méthode %2s. \nVérifiez dans la documentation produit ou dans l'API JDBC qu'il s'agit de l'argument approprié. " }, { "JZ014", "Vous ne pouvez pas définir setTransactionIsolation(Connection.TRANSACTION_NONE). \nCe niveau ne peut pas être défini ; il doit être renvoyé par un serveur." }, { "JZ00B", "Overflow numérique." }, { "JZ0C0", "Connexion déjà fermée." }, { "JZ0C1", "Une erreur IOException a fermé la connexion." }, { "JZ0S1", "Instruction sur l'état de la machine : tentative d'exécution de FETCH après instruction IDLE." }, { "JZ0S2", "L'objet Statement a déjà été fermé." }, { "JZ0R0", "ResultSet a déjà été fermé." }, { "JZ0R1", "L'état de ResultSet est IDLE, car vous ne tentez pas d'accéder à une ligne." }, { "JZ0R2", "Aucun jeu de résultats pour cette requête." }, { "JZ0R5", "Le ResultSet est actuellement positionné après la dernière colonne.Impossible d'effectuer une opération get* pour lire des données dans cet état" }, { "S0022", "'%1s' est un nom de colonne incorrect." }, { "JZ0R3", "La colonne est DEAD. Il s'agit d'une erreur interne. Contactez le Support Technique de Sybase." }, { "JZ0R4", "La colonne ne possède pas de pointeur de texte. Soit il ne s'agit pas d'une colonne de type text/image, soit la colonne a la valeur NULL" }, { "JZ0P1", "Type de résultat inattendu." }, { "JZ0P4", "Erreur de protocole. Ce message signale un problème interne. Contactez le Support Technique de Sybase." }, { "JZ0I5", "Une propriété CHARSET inconnue a été spécifiée : %1s." }, { "JZ0I6", "Une erreur s'est produite durant la conversion d'UNICODE dans le jeu de caractères du serveur. Message d'erreur : %1s" }, { "JZ0IB", "Le jeu de caractères par défaut du serveur de %1s ne peut se mapper sur un codage disponible dans l'environnement client de Java. Puisque jConnect ne peut effectuer une conversion côté client, la connexion n'est pas utilisable et est en cours de fermeture. Essayez avec une version de Java ultérieure ou en plaçant votre fichier i18n.jar ou charsets.jar d'installation Java dans le chemin d'accès à la classe." }, { "JZ0TD", "ThreadDeath détecté." }, { "JZ0I7", "Aucune réponse de la passerelle proxy." }, { "JZ0I8", "Connexion refusée par la passerelle proxy. Message de la passerelle : %1s" }, { "JZ0IA", "Erreur de troncature." }, { "JZ0TS", "Erreur de troncature lors de la tentative d'envoi de %1s." }, { "JZ0I9", "InputStream fermé." }, { "JZ0S8", "Séquence d'échappement incorrecte dans une requête SQL : '%1s'." }, { "JZ0SH", "Une fonction statique escape a été utilisée, mais les informations du descripteur d'accès aux métadonnées sont introuvables sur ce serveur " }, { "JZ0SI", "La fonction statique escape utilisée, %1s, n'est pas supportée par ce serveur." }, { "JZ0SJ", "Les informations du descripteur d'accès aux métadonnées sont introuvables dans cette base de données. Veuillez installer les tables requises comme indiqué dans la documentation jConnect." }, { "010UF", "Echec de la tentative d'exécution de la commande use database. Message d'erreur : %1s" }, { "010DF", "Echec de la tentative de définition de la base de données à la connexion.  Message d'erreur : %1s" }, { "010MX", "Les informations du descripteur d'accès aux métadonnées sont introuvables dans cette base de données. Veuillez installer les tables requises comme indiqué dans la documentation jConnect. L'erreur suivante a été détectée lors de la tentative d'extraction de métadonnées : %1s." }, { "010SJ", "Les informations du descripteur d'accès aux métadonnées sont introuvables dans cette base de données. Veuillez installer les tables requises comme indiqué dans la documentation jConnect." }, { "JZ0SA", "Instruction préparée : paramètre d'entrée non configuré, index : %1s." }, { "JZ0SB", "L'index de paramètre est en dehors de l'intervalle admis : %1s." }, { "JZ0S3", "La méthode récupérée %1s ne peut pas être réutilisée dans cette sous-classe." }, { "JZ0SC", "Callable Statement : vous avez tenté de définir l'état renvoyé comme paramètre d'entrée." }, { "01S09", "Vous ne pouvez pas utiliser la méthode de transaction locale %1s lorsqu'une transaction globale est active sur cette connexion." }, { "01S10", "Vous ne pouvez pas utiliser la méthode de transaction locale %1s sur une connexion de type XA antérieure au système 12." }, { "JZ0SD", "Aucun paramètre enregistré n'a été détecté parmi les paramètres de sortie." }, { "JZ0SE", "Type d'objet incorrect (ou objet null) spécifié pour setObject()." }, { "JZ0ST", "jConnect ne peut envoyer un objet Java comme paramètre littéral dans une requête. Assurez-vous que votre serveur de base de données prend en charge les objets Java et que la propriété de connexion LITERAL_PARAMS est définie sur false lorsque vous lancez cette requête." }, { "JZ0SF", "Aucun paramètre attendu. Vérifiez que la requête a été émise." }, { "JZ0SG", "CallableStatement a renvoyé moins de paramètres de sortie que prévu pour l'application." }, { "JZ0SU", "Un paramètre de date ou de Timestamp a été défini avec une année de %1s, mais le serveur ne prend en charge que des valeurs d'années entre %2s et %3s. Si vous essayez d'expédier des colonnes ou des paramètres timestamp ou date à date sur Adaptive Server Où que ce soit, il est préférable d'expédier vos données sous forme de chaînes, et de laisser le serveur les convertir." }, { "JZ0P7", "La colonne n'a pas été mise en cache ; utilisez la propriété RE-READABLE_COLUMNS." }, { "JZ0S4", "Impossible d'exécuter une requête entièrement vide." }, { "JZ0SM", "jConnect n'a pas pu exécuter une procédure stockée parce que il y a eu un problème d'expédition du(des) paramètre(s). Ce problème a sans doute été causé parce que le serveur ne prend pas en charge ce type de données, ou parce que jConnect n'a pas demandé de prise en charge pour ce type de données au moment de la connexion.\n.Essayez de régler la propriété de connexion de JCONNECT_VERSION à une valeur plus grande. Ou, si possible, essayez d'expédier votre commande d'exécution de procédure sous forme d'instruction de langage." }, { "JZ0SL", "Type SQL non supporté : %1s." }, { "JZ0SN", "setMaxFieldSize : la taille de ce champ ne peut pas être négative." }, { "JZ0SR", "setMaxRows : le nombre max. de lignes ne peut pas être négatif." }, { "JZ0SS", "setQueryTimeout : le délai d'exécution de la requête ne peut pas être négatif." }, { "ZZ00A", "La méthode %1s ne s'est pas achevée et n'aurait pas dû être appelée." }, { "JZ0NS", "La méthode %1s n'est pas supportée et n'aurait pas dû être appelée." }, { "010AF", "AVERTISSEMENT : la déclaration a échoué. Veuillez utiliser devclasses pour déterminer la cause de cette erreur grave. Message d'erreur : %1s" }, { "JZ0AF", "DECLARATION : échec de [%1s] sur classe %2s dans le thread %3s." }, { "JZ0P8", "Le nom du type de colonne RSMDA demandé est inconnu. Il s'agit d'une erreur interne Sybase. Contactez le Support Technique." }, { "010P4", "Un paramètre de sortie reçu a été ignoré." }, { "010P6", "Une ligne reçue a été ignorée." }, { "JZ011", "Exception de format détectée durant l'analyse de la propriété de connexion %1s." }, { "JZ012", "Erreur interne. Veuillez la signaler au Support Technique de Sybase. %1s n'est pas un type d'accès admis par la propriété de connexion." }, { "010UP", "La propriété de connexion %1s est inconnue et a été ignorée." }, { "JZ0J0", "Les valeurs d'offset et/ou de longueur excèdent la longueur text/image effective." }, { "010DP", "La propriété de connexion dupliquée, %1s, a été ignorée." }, { "010SK", "La base de données ne peut pas définir l'option de connexion %1s." }, { "JZ0PA", "La requête a été annulée et la réponse a été supprimée. L'annulation a probablement été émise par une autre instruction de cette connexion." }, { "JZ0T2", "Erreur de lecture du thread du service récepteur. Contrôlez les communications réseau." }, { "JZ0EM", "Fin des données." }, { "JZ0T7", "Erreur de lecture du thread du service récepteur : ThreadDeath détecté. Contrôlez les communications réseau." }, { "JZ0T8", "Les données reçues sont destinées à une requête inconnue. Contactez le Support Technique de Sybase." }, { "JZ0T3", "L'opération de lecture a dépassé le délai imparti." }, { "JZ0TO", "L'opération de lecture a dépassé le délai imparti." }, { "JZ0T4", "L'opération d'écriture a dépassé le délai imparti. Délai (en ms.) : %1 s." }, { "JZ0T5", "Le cache est plein. Utilisez la valeur par défaut ou une valeur supérieure pour STREAM_CACHE_SIZE." }, { "JZ0T6", "Erreur lors de la lecture de l'URL TDS encapsulé." }, { "JZ0H0", "Impossible de démarrer un thread pour le gestionnaire d'événement ; nom de l'événement : %1s." }, { "JZ0H1", "Une notification d'événement a été reçue, mais le gestionnaire d'événement est introuvable ; nom de l'événement : %1s." }, { "010SL", "Détection d'informations de descripteur d'accès aux métadonnées obsolètes dans cette base de données. Demandez à votre administrateur de base de données de charger les scripts les plus récents." }, { "JZ0SK", "oj escape n'est pas supporté par ce type de serveur de base de données. Contournement : utilisez une syntaxe de jointure externe propre au serveur, si les jointures sont supportées.  Consultez la documentation du serveur." }, { "JZ00L", "Echec de la connexion. Examinez les SQLWarnings associées à cette exception pour la ou les raisons suivantes :" }, { "JZ00M", "La connexion a dépassé le délai imparti. Vérifiez si le serveur de base de données s'exécute bien sur l'hôte et avec le numéro de port que vous avez spécifiés. Vérifiez également si le serveur de base de données ne présente pas d'autre condition (tempdb saturé, par exemple) susceptible de causer sa suspension d'activité." }, { "010HD", "Le mode de reprise sur le serveur secondaire HD de Sybase n'est pas supporté par ce type de serveur de base de données." }, { "JZ0F1", "Une connexion en mode reprise sur le serveur secondaire HD de Sybase a été demandée sans que l'adresse de serveur compagnon ne soit spécifiée." }, { "JZ0F2", "Une reprise haute disponibilité Sybase est survenue. La transaction en cours est interrompue, mais la connexion est toujours utilisable. Tentez la transaction à nouveau." }, { "010HA", "La demande de session HD a été rejetée par le serveur. Veuillez reconfigurer la base de données ou travailler hors session HD." }, { "0100V", "La version du protocole TDS utilisé est trop ancienne. Version : %1s.%2s.%3s.%4s" }, { "010HT", "La propriété Hostname est tronquée, taille maximale admise : 30." }, { "010PO", "La propriété LITERAL_PARAM a été redéfinie à 'false', car DYNAMIC_PREPARE été définie à 'true'." }, { "010SP", "Ce fichier ne peut pas être ouvert en écriture. Fichier : %1s. Message d'erreur : %2s" }, { "010TP", "Le jeu de caractères initial appliqué à la connexion, %1s, n'a pas pu être converti par le serveur. Le jeu de caractères proposé par le serveur, %2s, va être utilisé, ainsi que les conversions effectuées par jConnect." }, { "010TQ", "jConnect n'a pas pu déterminer le jeu de caractères par défaut du serveur Cela est sans doute dû à un problème de métadonnées.\n.Veuillez installer les tables requises comme indiqué dans la Documentation jConnect.\nqui ne gère que les caractères compris entre 0x00 et 0x7F." }, { "010SN", "Autorisation en écriture refusée sur ce fichier. Fichier : %1s. Message d'erreur : %2s" }, { "010SM", "Cette base de données ne supporte pas les fonctionnalités proposées. Nouvelle tentative en cours." }, { "JZ010", "Impossible de désérialiser une valeur d'objet. Message d'erreur : %1s" }, { "JZ0PB", "Le serveur ne supporte pas l'opération demandée." }, { "010SQ", "Connexion ou login refusé. Nouvelle tentative de connexion avec l'adresse d'hôte/port suivante." }, { "JZ0D7", "Erreur lors de l'appel du fournisseur de l'URL %1s. Message d'erreur : %2s" }, { "JZ013", "Erreur lors de la recherche de l'entrée JNDI : %1s. Message d'erreur : %2s" }, { "JZ0NF", "Impossible de charger une instance de com.sybase.jdbcx.SybSocketFactory.  Vérifiez la propriété SYBSOCKET_FACTORY et assurez-vous que le nom de la classe est correct, que le package est entièrement spécifié, que la classe est disponible via le chemin d'accès spécifié et qu'un constructeur public sans arguments est indiqué." }, { "JZ0US", "La propriété de connexion SYBSOCKET_FACTORY a été définie et l'URL de servlet a été affecté à la propriété de connexion PROXY. Le pilote jConnect ne supporte pas cette combinaison. Pour transmettre en HTTP sécurisé depuis une applet exécutée dans un explorateur, utilisez un URL de proxy commençant par 'https://'." }, { "JZ0BI", " setFetchSize : La taille de lecture doit être définie dans les limites suivantes : 0 <= lignes <= (nombre maximum de lignes dans ResultSet)." }, { "JZ0BJ", "La valeur définie pour la propriété de connexion IMPLICIT_CURSOR_FETCH_SIZE doit être > 0." }, { "JZ0PC", "Le nombre et la taille des paramètres de votre requête nécessitent le support de tables larges. Ce serveur n'offre pas ce support ou il n'a pas été demandé lors de la connexion. Attribuez une valeur >= 6 à la propriété JCONNECT_VERSION pour activer le support de tables larges." }, { "JZ0PD", "La taille de la requête de votre préparation dynamique nécessite le support de tables larges. Ce serveur n'offre pas ce support ou il n'a pas été demandé lors de la connexion. Attribuez une valeur >= 6 à la propriété JCONNECT_VERSION pour activer le support de tables larges." }, { "JZ0PE", "Le nombre de colonnes spécifiées dans la déclaration du curseur OU la taille de cette déclaration elle-même nécessitent le support de tables larges. Ce serveur n'offre pas ce support ou il n'a pas été demandé lors de la connexion. Attribuez une valeur >= 6 à la propriété JCONNECT_VERSION pour activer le support de tables larges." }, { "JZ0BI", " setFetchSize : La taille de lecture doit être définie dans les limites suivantes : 0 <= lignes <= (nombre maximum de lignes dans ResultSet)." }, { "JZ0NF", "Impossible de charger une instance de com.sybase.jdbcx.SybSocketFactory.  Vérifiez la propriété SYBSOCKET_FACTORY et assurez-vous que le nom de la classe est correct, que le package est entièrement spécifié, que la classe est disponible via le chemin d'accès spécifié et qu'un constructeur public sans arguments est indiqué." }, { "JZ0SO", "Le type de synchronisation de ResultSet est incorrect : %1s" }, { "JZ0SP", "Type ResultSet incorrect : %1s" }, { "JZ0SQ", "Type UDT incorrect : %1s" }, { "JZ0BS", "Instructions par batch non supportées" }, { "JZ0BE", "BatchUpdateException : une erreur est survenue lors de l'exécution de l'instruction par batch : %1s" }, { "JZ0BP", "Les paramètres de sortie ne sont pas admis dans les instructions Batch Update" }, { "010PF", "Un ou plusieurs fichiers jar spécifiés dans la propriété de connexion PRELOAD_JARS n'ont pas pu être chargés." }, { "JZ0XS", "Le serveur ne supporte pas les transactions de type XA. Veuillez vérifier que la fonction de transaction est activée et autorisée sur ce serveur." }, { "JZ0XC", "%1s est un type de coordinateur de transaction inconnu." }, { "JZ0XU", "L'utilisateur courant n'est pas habilité à exécuter des transactions de type XA. Veillez à ce que l'utilisateur dispose du rôle %1s." }, { "JZ0CL", "Définissez la propriété CLASS_LOADER avant d'utiliser la propriété PRELOAD_JARS." }, { "JZ0GS", "Une exception d'API Generic Security Services s'est produite. Le code d'erreur majeure est %1s.\nLe message d'erreur majeure est le suivant : %2s\nLe code d'erreur mineure est %3s.\nLe message d'erreur mineure est le suivant : %4s" }, { "010HN", "Le client n'a pas spécifié de propriété de connexion SERVICE_PRINCIPAL_NAME. Par conséquent, jConnect utilise le nom d'hôte de %1s comme nom principal de service." }, { "010KF", "Le serveur a rejeté votre tentative de connexion Kerberos. Cela est probablement dû à une exception GSS (Generic Security Services). Vérifiez l'environnement et la configuration Kerberos." } };
/*     */ 
/*     */   public Object[][] getContents()
/*     */   {
/* 965 */     return CONTENTS;
/*     */   }
/*     */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.jdbc.resource.Messages_fr
 * JD-Core Version:    0.5.4
 */