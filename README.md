# SubTranslator
Juste une interface pour traduire manuellement un fichier ->srt<- ; Fait pour m'entrainer aux servlets

Apache Tomcat 8

A modifier pour etre utilisé :
=> Créer la BDD avec le fichier sql à la racine
=> les données relatives à votre BDD SQL (base, user, password) dans com.subTranslator.dao.DaoFactory
=> la balise <location> dans le WebContent/WEB-INF/web.xml

Pour importer sous Eclipse, en cas de problème :
=> unzip le projet dans votre workspace
=> supprimer .classpath / .project et le dossier .settings
=> Sous eclipse : File->New->Dynamic Web Project
=> Créer un "nouveau" projet avec le nom SubTranslator ; ça reprendra les fichiers comme il faut normalement

C'est pas optimal, mais ça répond à la consigne farfelue qui était donnée.
