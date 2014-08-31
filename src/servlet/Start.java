package servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.util.InputMismatchException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import main.CreateAuthorizeURL;
import main.RetrieveAllSpotifyTracks;
import main.Settings;

/**
 * Servlet implementation class Start
 */
@WebServlet("/Start")
public class Start extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public Start() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		PrintWriter out = response.getWriter();
		out.println("<HTML>");
		out.println("<HEAD>");
		out.println("<TITLE>" + ToolsHTML.TITLE + "</TITLE>");
		out.println("</HEAD>");
		out.println("<BODY>");
		out.println("<form method=\"post\" action=\"Start\">");
		out.println("Enter date to get the spotify playlist from that day speaker's playlist or some url from SR.se website on order to get the generated playlist <br>"
				+ "(ex: 2014-07-25 or http://sverigesradio.se/sida/latlista.aspx?programid=4131&date=2014-08-07: <input type=\"text\" value=\"2014-07-05\" name=\"srInput\">");
		out.println("<input type=\"submit\" value=\"Generate playlist!\">");
		out.println("</BODY>");
		out.println("</HTML>");
		out.close();
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");

		String input = request.getParameter("srInput");
		String url = makeUrl(input);
		if (url == null) {
			ToolsHTML.showError(request, response, input + " is wrong input");
			return;
		}
		RetrieveAllSpotifyTracks retrieveAllSpotifyTracks;
		try {
			retrieveAllSpotifyTracks = new RetrieveAllSpotifyTracks(url);
		} catch (InputMismatchException | ParseException e) {
			ToolsHTML.showError(request, response, e.getMessage());
			e.printStackTrace();
			return;
		}
		PrintWriter out = response.getWriter();
		out.println("<html>");
		out.println("<head><title>Sommar i P1 to Spotify!</title></head>");
		out.println("<br>Downloading all the tracks from "
				+ url
				+ ", meanwhile you need to login to Spotify in order to create the playlist<br>");
		out.println("<a href=\"");
		CreateAuthorizeURL.send(url, out);
		out.println("\">Login to Spotify</a>");
		out.println("<body>");
		out.close();
		retrieveAllSpotifyTracks.start();
	}

	private String makeUrl(String input) {
		if (input.contains("http://sverigesradio.se")) {
			return input;
		} else {
			String[] sDate = input.split("[-]");
			for (String s : sDate) {
				try {
					Integer.parseInt(s);
				} catch (NumberFormatException e) {
					return null;
				}
			}
			return Settings.urlToSiP1 + input;
		}

	}

}
