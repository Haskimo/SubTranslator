package com.subTranslator.servlets;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;



/**
 * Servlet implementation class Uploader
 */
@WebServlet("/Uploader")
public class Uploader extends HttpServlet {
	private static final long serialVersionUID = 1L;
	public static final int BUFFER_SIZE = 10240;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Uploader() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		this.getServletContext().getRequestDispatcher("/WEB-INF/upload.jsp").forward(request, response);
	}

	/**
	 * Télécharge le fichier SRT sur le serveur. Fournit le PATH local sur le serveur du fichier téléchargé et le donne à la requête (.forward)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String LOCAL_PATH = getServletContext().getRealPath("WEB-INF/../");		
		
		Part part = request.getPart("fichier"); 	// On selectionne le Http.Part de la requête qui concerne le champ "fichier"
		String nomFichier = getFileName(part); 		// Appel de la méthode qui va parser le nom du fichier dans ce Http.Part
		
		
		if (nomFichier == null || nomFichier.isEmpty())
		{
			request.setAttribute("erreur", "Erreur-Requête : Pas de fichier trouvé dans la requête");
		} else {
			// Il y a bien un fichier, on vérifie son extension (il faut .srt)
            if (!nomFichier.substring(nomFichier.lastIndexOf(".")).equals(".srt")) {
                request.setAttribute("erreur", "Erreur-Fichier : Le fichier doit être du type .srt");
            } else {
            	String nomChamp = part.getName();	// Récupère le nom du champ de la Http.Part (ici c'est "fichier")
    			
            	// IE bug fix
            	nomFichier = nomFichier.substring(nomFichier.lastIndexOf('/') + 1).substring(nomFichier.lastIndexOf('\\') + 1);
    			
    			try {
    				writeFile(part, nomFichier, LOCAL_PATH);
    				request.setAttribute(nomChamp, nomFichier);
    				request.setAttribute("reussite", "Fichier chargé avec succès. (at : " + LOCAL_PATH + nomFichier + ").");
    			} catch (IOException e) {
    				
    			}
            }
		}
		
		this.getServletContext().getRequestDispatcher("/WEB-INF/upload.jsp").forward(request, response);
	}
	
	/**
	 * @param : part->contient le fichier ; nomFichier->contient le nom du fichier ; cheminFichier->contient le chemin local/serveur pour le fichier
	 */
	private void writeFile(Part part, String nomFichier, String cheminFichier) throws IOException {
		BufferedInputStream input = null;
        BufferedOutputStream output = null;
        
        try {
            input = new BufferedInputStream(part.getInputStream(), BUFFER_SIZE);
            output = new BufferedOutputStream(new FileOutputStream(new File(cheminFichier + nomFichier)), BUFFER_SIZE);

            byte[] tampon = new byte[BUFFER_SIZE];
            int longueur;
            while ((longueur = input.read(tampon)) > 0) {
                output.write(tampon, 0, longueur);
            }
        } finally {
            try {
                output.close();
            } catch (IOException ignore) {
            	
            }
            try {
                input.close();
            } catch (IOException ignore) {
            	
            }
        }
	}
	
	/**
	 * @return sous-chaine de caractères qui correspond au nom de fichier dans l'objet Part "part"
	 */
	private static String getFileName(Part part)
	{
		for (String contentDisposition : part.getHeader("content-disposition").split(";")) // Parmi tous les champs
		{
			if (contentDisposition.trim().startsWith("filename")) // Si on a un champ filename
				return contentDisposition.substring(contentDisposition.indexOf('=') + 1).trim().replace("\"", ""); // On le retourne
		}
		return null; // S'il n'y a aucun champ "filename", on retourne null
	}

}
