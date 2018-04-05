package com.subTranslator.servlets;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.subTranslator.dao.DaoException;
import com.subTranslator.dao.DaoFactory;
import com.subTranslator.dao.SubtitleDao;

/**
 * Servlet implementation class EditSRT
 */
@WebServlet("/EditSRT")
public class EditSRT extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private SubtitleDao subtitleDao;
	private String cheminFichier = null;
	private String nomFichier = null;
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public EditSRT() {
    	DaoFactory daoFactory = DaoFactory.getInstance();
        this.subtitleDao = daoFactory.getSubtitleDao();
        
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		ServletContext context = getServletContext();
		// on récupère le chemin du fichier par rapport au contexte du serveur; on récupère le nom dans la requête
		this.cheminFichier = context.getRealPath("WEB-INF/../");		
		this.nomFichier = request.getParameter("fichier");
		request.setAttribute("nomFichier", this.nomFichier);
		
		try {
			subtitleDao.creerBase(this.cheminFichier+this.nomFichier);
		} catch (DaoException e) {
			e.printStackTrace();
		}
				
		try {
			// En passant par DAO, la servlet n'a aucune idée qu'elle fait du SQL
			request.setAttribute("subtitles", subtitleDao.lister());
		}
		catch (DaoException e) {
			request.setAttribute("erreur", e.getMessage());
		}
		
		//SubtitlesHandler controleur = new SubtitlesHandler(cheminFichier+nomFichier);
		//request.setAttribute("subtitles", controleur.getSubtitles());
		
		
		this.getServletContext().getRequestDispatcher("/WEB-INF/editsrt.jsp").forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		ServletContext context = getServletContext();
		String traduction = "";
		
		try {
			for (int i = 1; i <= subtitleDao.lister().size(); i++) {
				traduction = request.getParameter("ligne" + Integer.toString(i));
				if ( traduction != null && traduction != "") { // Si on a une entrée dans le champ
					subtitleDao.traduire(i, traduction);
				}
			}
		} catch (DaoException e) {
			request.setAttribute("erreur", e.getMessage());
		}
		
		try {
			subtitleDao.ecrireTrad(this.cheminFichier + "trad_" + this.nomFichier);
		} catch (DaoException e) {
			request.setAttribute("erreur", e.getMessage());
		}
		
		// Telecharger le fichier qui est sur le serveur (this.cheminFichier + "trad_" + this.nomFichier)
		String cheminFichier = this.cheminFichier + "trad_" + this.nomFichier;
		
		File downloadFile = new File(cheminFichier);
		FileInputStream inStream = new FileInputStream(downloadFile);
		
		String mimeType = context.getMimeType(cheminFichier);
		if (mimeType == null) {
			mimeType = "application/octet-stream";
		}
		response.setContentType(mimeType);
		response.setContentLength((int) downloadFile.length());
		
		String headerKey = "Content-Disposition";
        String headerValue = String.format("attachment; filename=\"%s\"", downloadFile.getName());
        response.setHeader(headerKey, headerValue);
        
        OutputStream outStream = response.getOutputStream();
        byte[] buffer = new byte[4096];
        int bytesRead = -1;
         
        while ((bytesRead = inStream.read(buffer)) != -1) {
            outStream.write(buffer, 0, bytesRead);
        }
         
        inStream.close();
        outStream.close(); 
	}

}
