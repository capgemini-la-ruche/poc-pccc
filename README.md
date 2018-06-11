# Plateforme Collaborative de Compétences et Contenus (PCCC)

<ul>
  <li>Ne rien commiter dans la branche <b>master</b>. Elle sert aux livraisons</li>
  <li>La branche <b>develop</b> est la version commune - qui doit fonctionner ;) - la plus à jour</li>
  <li>Pour travailler sur une fonctionnalité, copier <b>develop</b> dans une nouvelle branche, par exemple <b>dev-page-des-contenus</b>. 
   Une fois que le code fonctionne, merger dans <b>develop</b></li>
</ul>

<H3>Pour lancer l'appli</h3>
<ul>
  <li>La VM dans le cloud Heroku n'héberge que <b>la BDD postgreSQL</b></li>
  <li>Pour lancer l'appli il vous faut le plugin <b>JettyRunner</b> (IntelliJ) ou <b>Eclipse Jetty Plugin</b></li>
 <li>- packager le WAR avec maven <b>install</b></li>
 <li>- puis pour lancer le Jetty dans votre IDE </li>
</ul>

<H3>Pour développer plusieurs outils peuvent vous être utile</h3>
<ul>
  <li><b>Agent Ransack</b> : menu contextuel, recherche multi-critère, multi-répertoires, regex...</li>
  <li><b>PgAdmin</b> : accès à la BDD en mode admin (reinstaller le schéma, etc...)</li>
  <li><b>SquirreL SQL : client JDBC partique</b></li>
  <li><b>D'autres ? A compléter</b></li>
</ul>

<H3>Les URLs qui fonctionnent actuellement (jetty lancé)</H3>
<ul>
  <li>http://localhost:8080/pcccManager/  sans doute le future IHM de la PCCC</li>
  <li>http://localhost:8080/pcccManager/info  infos techniques diverses</li>
  <li>http://localhost:8080/pcccManager/dao?action=update  Not implemented</li>
  <li>http://localhost:8080/pcccManager/dao?action=select&queryName=tags  <br/>
    C'est un mode REST pour interroger la BDD<br/>
    Le "queryName" correspond au nom de la requête SQL à exécuter : pccc.sql.queries.properties<br/>
    La réponse revient en JSON.<br/></li>
  <li>http://localhost:8080/pcccManager/dao?action=select&queryName=resourceByWorkLocation<br/>
    pccc.sql.select.resourceByWorkLocation<br/>
    pccc.sql.select.resourceByWorkLocation.aggColumnId = 1 n° de colonne sur laquelle on veut l'aggregation.<br/>
    Essayez différentes valeurs<br/>
    A partir de ses docs json, le framework javascript doit afficher la page telle que proposé dans la maquette Excel</li>
</ul>
