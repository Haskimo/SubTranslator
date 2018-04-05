package com.subTranslator.dao;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.subTranslator.beans.Subtitle;

public class SubtitleDaoImpl implements SubtitleDao {
	private DaoFactory daoFactory;
	private List<Subtitle> subtitles = new ArrayList<Subtitle>();
	
	SubtitleDaoImpl(DaoFactory daoFactory) {
        this.daoFactory = daoFactory;
    }
	
	@Override // Parcours le fichier et enregistre les sous-titres dans la BDD
	public void creerBase(String fichier) throws DaoException {
		
		this.viderBase();
		BufferedReader br;
		try {
			System.out.println("Fichier à lire : " + fichier);
			br = new BufferedReader(new FileReader(fichier));
			String line;
			boolean id_ok, duree_ok, original_ok;
			id_ok = duree_ok = original_ok = false;
			int id = 0;
			String duree, original;
			duree = original = "";
			
			while ((line = br.readLine()) != null) {
				if (line.matches("^\\d{1,5}$")) { // ligne numero de sous titre
					id = Integer.parseInt(line);
					id_ok = true;
				} else if (id_ok && line.matches("^\\d{2,3}:\\d{2}:\\d{2},\\d{3} --> \\d{2,3}:\\d{2}:\\d{2},\\d{3}$")){ // ligne duree
					duree = line;
					duree_ok = true;
				} else if (id_ok && duree_ok && line.matches("^.+$")) { // Une chaine de caractere d'au moins 1 caractere
					original = line;
					// verifier la ligne suivante : si vide ou null : rien ; sinon concatenation +=
					line = br.readLine();
					if ( line != null && !line.isEmpty()) {
						original = original + "\n" + line;
					}
					original_ok = true;
				}
				
				if (id_ok && duree_ok && original_ok) {
					// creer entree bdd
					Subtitle sub = new Subtitle();
	                sub.setId(id);
	                sub.setDuree(duree);
	                sub.setOriginal(original);
	                sub.setTraduction(""); // pour l'instant, pas de traduction car on parle du fichier original
	                this.ajouter(sub);
	                
					// Remettre les paramètres locaux à 0 pour une nouvelle entrees

					id_ok = duree_ok = original_ok = false;
					duree = original = "";
				}
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	@Override // Ajoute un sous-titre à la BDD
	public void ajouter(Subtitle sub) throws DaoException {
		Connection connexion = null;
        PreparedStatement preparedStatement = null;

        try {
            connexion = daoFactory.getConnection(); // Récupère la connection déjà faite avec le DaoFactory
            preparedStatement = connexion.prepareStatement("INSERT INTO soustitres(id, duree, original, traduction) VALUES(?, ?, ?, ?);");
            
            // On donne les paramètres de l'objet java sub pour créer l'entrée en BDD
            preparedStatement.setInt(1, sub.getId());
            preparedStatement.setString(2, sub.getDuree());
            preparedStatement.setString(3, sub.getOriginal());
            preparedStatement.setString(4, sub.getTraduction());

            preparedStatement.executeUpdate();
            connexion.commit(); // Gestion de la transaction
            this.subtitles.add(sub);
            
        } catch (SQLException e) {
        	try {
                if (connexion != null) {
                    connexion.rollback(); // Si problème dans la transaction, on rollback
                }
            } catch (SQLException e2) {
            }
            throw new DaoException("Impossible de communiquer avec la base de données"); // L'utilisateur n'a pas à savoir que c'est du SQL, donc DAOException
        } finally {
        	try {
                if (connexion != null) {
                    connexion.close();  
                }
            } catch (SQLException e) {
                throw new DaoException("Impossible de communiquer avec la base de données");
            }
        }
	}

	@Override // Crée les objets java à partir de la lecture de la BDD
	public List<Subtitle> lister() throws DaoException {
		List<Subtitle> subtitles_base = new ArrayList<Subtitle>();
        Connection connexion = null;
        Statement statement = null;
        ResultSet resultat = null;

        try {
            connexion = daoFactory.getConnection();
            statement = connexion.createStatement();
            resultat = statement.executeQuery("SELECT id, duree, original, traduction FROM soustitres;");

            while (resultat.next()) {
                int id = resultat.getInt("id");
                String duree = resultat.getString("duree");
                String original = resultat.getString("original");
                String traduction = resultat.getString("traduction");

                Subtitle sub = new Subtitle();
                sub.setId(id);
                sub.setDuree(duree);
                sub.setOriginal(original);
                sub.setTraduction(traduction);

                subtitles_base.add(sub);
            }
            this.subtitles = subtitles_base;
        } catch (SQLException e) {
        	throw new DaoException("Impossible de communiquer avec la base de données pour la requête SELECT");
        }
        finally {
            try {
                if (connexion != null) {
                    connexion.close();  
                }
            } catch (SQLException e) {
                throw new DaoException("Impossible de communiquer avec la base de données pour la fermeture de la connexion");
            }
        }
        return this.subtitles;
    }

	@Override
	public void viderBase() throws DaoException {
		Connection connexion = null;
        PreparedStatement preparedStatement = null;

        try {
            connexion = daoFactory.getConnection(); // Récupère la connection déjà faite avec le DaoFactory
            preparedStatement = connexion.prepareStatement("DELETE FROM soustitres;");
            preparedStatement.executeUpdate();
            connexion.commit(); // Gestion de la transaction
        } catch (SQLException e) {
        	try {
                if (connexion != null) {
                    connexion.rollback(); // Si problème dans la transaction, on rollback
                }
            } catch (SQLException e2) {
            }
            throw new DaoException("Impossible de communiquer avec la base de données"); // L'utilisateur n'a pas à savoir que c'est du SQL, donc DAOException
        } finally {
        	try {
                if (connexion != null) {
                    connexion.close();  
                }
            } catch (SQLException e) {
                throw new DaoException("Impossible de communiquer avec la base de données");
            }
        }
	}

	@Override
	public void traduire(int i, String traduction) throws DaoException {
		Connection connexion = null;
        PreparedStatement preparedStatement = null;

        try {
            connexion = daoFactory.getConnection(); // Récupère la connection déjà faite avec le DaoFactory
            preparedStatement = connexion.prepareStatement("UPDATE soustitres SET traduction = ? WHERE id = ?;");
            preparedStatement.setString(1, traduction);
            preparedStatement.setInt(2, i);

            preparedStatement.executeUpdate();
            connexion.commit(); // Gestion de la transaction
            
            // MAJ this.subtitles
            for (Subtitle sub: this.subtitles) {
            	if (sub.getId() == i) {
            		sub.setTraduction(traduction);
            	}
            }
        } catch (SQLException e) {
        	try {
                if (connexion != null) {
                    connexion.rollback(); // Si problème dans la transaction, on rollback
                }
            } catch (SQLException e2) {
            }
            throw new DaoException("Impossible de communiquer avec la base de données"); // L'utilisateur n'a pas à savoir que c'est du SQL, donc DAOException
        } finally {
        	try {
                if (connexion != null) {
                    connexion.close();  
                }
            } catch (SQLException e) {
                throw new DaoException("Impossible de communiquer avec la base de données");
            }
        }
	}

	@Override
	public void ecrireTrad(String fichier) throws DaoException {
		List<Subtitle> subtitles_base = this.lister();
		
		BufferedWriter bw;
		try {
			bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fichier), StandardCharsets.UTF_8));
			
			for(Subtitle sub: subtitles_base) {
				if (sub.getTraduction() == null || sub.getTraduction().isEmpty()) {
					bw.write(String.valueOf(sub.getId())+"\n"+sub.getDuree()+"\n"+sub.getOriginal()+"\n\n");
				} else {
					bw.write(String.valueOf(sub.getId())+"\n"+sub.getDuree()+"\n"+sub.getTraduction()+"\n\n");
				}		
			}
			
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
