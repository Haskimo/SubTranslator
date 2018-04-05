package com.subTranslator.dao;

import java.util.List;

import com.subTranslator.beans.Subtitle;

public interface SubtitleDao {
	void ajouter( Subtitle sub ) throws DaoException; // Ecrit dans la BDD
    List<Subtitle> lister() throws DaoException; // Lit la BDD et récupère dans un ArrayList<Subtitle>
	void creerBase(String fichier) throws DaoException; // Crée une BDD à partir du contenu du fichier
	void viderBase() throws DaoException; // SQL Delete
	void traduire(int i, String traduction) throws DaoException; // Update SQL et List<Subtitle>
	void ecrireTrad(String fichier) throws DaoException; //Ecrit et DL un fichier
}
