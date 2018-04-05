<%@ page pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
	<%@ include file="bootstrap.jsp" %>
	<title>Éditer les sous-titres du fichier</title>
</head>
<body>
	<a href="/SubTranslator/upload" class="btn btn-primary">Charger un autre fichier</a>
	<div class="container">
		<h2><c:out value="Votre fichier : ${ nomFichier }" /></h2>
		<form method="post" action="editsrt">    
	        <input type="submit" style="position:fixed; top: 10px; right: 10px;" value="Télécharger le fichier de traduction"/>       
		    <table class="table table-bordered">
			  <thead class="thead-dark">
			    <tr>
					<th scope="col">#ID</th>
				    <th scope="col" style="text-align:center;">Timer</th> 
				    <th scope="col">Contenu</th>
				    <th scope="col">Traduire</th>
				</tr>
		        <c:forEach items="${ subtitles }" var="line" varStatus="status">
		        	<tr>
		        		<th scope="row" style="text-align:center;"><c:out value="${ line.getId() }" /></th>
		        		<td><c:out value="${ line.getDuree() }" /></td>
		        		<td><c:out value="${ line.getOriginal() }" /></td>
		        		<td><input type="text" name="ligne${ line.getId() }" id="${ line.getId() }" size="35" value="${ line.getTraduction() }"/></td>
		        	</tr>
		    	</c:forEach>
		    </table>
	    </form>
	</div>
</body>
</html>