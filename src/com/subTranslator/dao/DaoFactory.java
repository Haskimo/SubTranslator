package com.subTranslator.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DaoFactory {
	private String url;
    private String username;
    private String password;
    
    DaoFactory(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }
    
    // Méthode statique qui appelle le constructeur en lui passant les paramètres liés à la BDD
    public static DaoFactory getInstance() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {

        }

        DaoFactory instance = new DaoFactory("jdbc:mysql://localhost:3306/subtranslator", "root", "123456");
        return instance;
    }
    
    public Connection getConnection() throws SQLException {
        Connection connexion = DriverManager.getConnection(url, username, password);
        connexion.setAutoCommit(false); // Pour gérer les transactions nous-mêmes
    	return connexion;
    }

    // Récupération du SubtitleDao
    public SubtitleDao getSubtitleDao() {
        return new SubtitleDaoImpl(this);
    }
}