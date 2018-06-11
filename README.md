# poc-pccc# poc-pccc
Plateforme Collaborative de Compétences et Contenus

<ul>
  <li>Ne rien commiter dans la branche <b>master</b>. Elle sert aux livraisons</li>
  <li>La branche <b>develop</b> est la version commune - qui doit fonctionner ;) - la plus à jour</li>
  <li>Pour travailler sur une fonctionnalité, copier <b>develop</b> dans une nouvelle branche, par exemple <b>dev-page-contenus</b>. 
   Une fois que le code fonctionne, merger dans <b>develop</b></li>
</ul>

<ul>
  <li>La VM dans le cloud Heroku n'héberge que la BDD postgresql</li>
  <li>Pour lancer l'appli il vous faut le plugin JettyRunner (IntelliJ) ou le "Eclipse Jetty Plugin"</li>
 <li>- packager le WAR avec maven </b>install</b></li>
 <li>- puis pour lancer le Jetty dans votre IDE </li>
</ul>

Pour développer plusieurs outils peuvent vous être utile :
<ul>
  <li><b>Agent Ransack</b> : menu contextuel, recherche multi-critère, multi-répertoires, regex...</li>
  <li><b>PgAdmin</b> : accès à la BDD en mode admin (reinstaller le schéma, etc...)</li>
  <li><b>SquirreL SQL : client JDBC partique</b></li>
  <li><b>D'autres ? A compléter</b></li>
</ul>

Les URLs qui répondent actuellement(une fois le jetty lancé) :
- http://localhost:8080/pcccManager/  sans doute le future IHM de la PCCC
- http://localhost:8080/pcccManager/info  infos techniques diverses
- http://localhost:8080/pcccManager/dao?action=update  Not implemented
- http://localhost:8080/pcccManager/dao?action=select&queryName=tags
    C'est un mode REST pour interroger la BDD.
    Le "queryName" correspond au nom de la requête SQL à exécuter : pccc.sql.queries.properties
    La réponse revient en JSON.

- http://localhost:8080/pcccManager/dao?action=select&queryName=resourceByWorkLocation
    pccc.sql.select.resourceByWorkLocation
    pccc.sql.select.resourceByWorkLocation.aggColumnId = 1 n° de colonne sur laquelle on veut l'aggregation.
    Essayez différentes valeurs, vous comprendrez mieux.

- A partir de ses docs json, le framework javascript doit afficher la page telle que proposé dans la maquette Excel.