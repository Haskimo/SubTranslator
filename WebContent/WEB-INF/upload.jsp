<%@ page pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
	<%@ include file="bootstrap.jsp" %>
	<title>Télécharger un fichier SRT sur le serveur</title>
</head>
<body>
	<div class="container">
		<h2>Selectionnez votre fichier à charger sur le serveur (.srt)</h2>
		<h3>Votre fichier : </h3>
		<c:choose>
			<c:when test="${ !empty reussite }">
				<p style="color:green;"><c:out value="${ reussite }" /></p>
				<a class="btn btn-success" href="/SubTranslator/editsrt?fichier=${ fichier }">Editer le fichier (${ fichier })</a>
			</c:when>
			<c:when test="${ empty reussite }">
				<c:if test="${ !empty erreur }"><p style="color:red;"><c:out value="${ erreur }" /></p></c:if>
				<form action="upload" method="post" enctype="multipart/form-data">
				    <label for="fichier">Emplacement du fichier</label>
				    <input type="file" name="fichier" />
				    <br>
				    <input type="submit" value="Charger sur le serveur"/>
				</form>
			</c:when>
		</c:choose>
	</div>
</body>
</html>