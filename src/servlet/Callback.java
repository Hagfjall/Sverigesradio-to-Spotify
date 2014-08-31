package servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import main.CreatePlaylist;
import main.Settings;
import main.Tools;

/**
 * Servlet implementation class TestServlet1
 */
@WebServlet({ "/Callback" })
public class Callback extends HttpServlet {

	private Logger logger;
	private static final long serialVersionUID = 1L;

	/**
	 * Default constructor.
	 */
	public Callback() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		logger = Logger.getLogger(Callback.class.getName()
				+ request.getRemoteUser());
		logger.finer("doGet uri: " + request.getRequestURI());
		response.setContentType("text/html");
		// scope=user-read-private%20user-read-email&state=programid=2071&date=2014-07-05
		String query = request.getQueryString();
		if (query == null) {
			ToolsHTML.showError(request, response, "No query");
			return;
		}
		String state = query.substring(query.indexOf("&state=") + 7);
		logger.fine("state from spotify " + state);
		String playlistFile = state;
		List<String> lines = Tools.IOtoDisk(false, playlistFile, null);
		if (lines == null) {
			ToolsHTML
					.showError(
							request,
							response,
							"No saved songs for this URL, probably slow connection to spotify "
									+ "or something wrong with the server...<br>"
									+ "Try again in a short while or report to admin [at ] hagfjall.se with the given URL (or date)");
			return;
		}
		CreatePlaylist spotifyUser = new CreatePlaylist(query);
		List<String> songs = new ArrayList<String>();
		List<String> spotifyUris = new ArrayList<String>();
		for (String line : lines) {
			String data[] = line.split(Settings.delimiter);
			songs.add(data[0]);
			spotifyUris.add(data[2]);
		}
		PrintWriter out = response.getWriter();

		out.println("<HTML>");
		out.println("<HEAD>");
		out.println("<TITLE>" + ToolsHTML.TITLE + "</TITLE>");
		out.println("</HEAD>");
		out.println("<BODY>");
		out.println("Hello ");
		spotifyUser.printName(out);
		out.println("!<br>Creating playlist with these songs:");
		br(out);
		for (int i = 0; i < songs.size(); i++) {
			if (!spotifyUris.get(i).equals("null")) {
				out.println(songs.get(i));
				br(out);
			}
		}
		br(out);
		out.println("Could <b>not</b> find any match on spotify for these songs: ");
		br(out);
		for (int i = 0; i < songs.size(); i++) {
			if (spotifyUris.get(i).equals("null")) {
				out.println(songs.get(i));
				br(out);
			}
		}

		out.println("</BODY>");
		out.println("</HTML>");
		out.close();
		spotifyUser.createPlaylist(spotifyUris);
	}

	private void br(PrintWriter out) {
		out.print("<br>");
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}
