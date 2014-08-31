package servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ToolsHTML {
	
	public static final String TITLE="Sverigesradio.se to Spotify!";

	protected static void showError(HttpServletRequest request,
			HttpServletResponse response, String msg) {
		Logger.getLogger(ToolsHTML.class.getName()).finer(
				"HTTP ERROR: " + request.getRemoteUser() + " MSG: " + msg);
		PrintWriter out;
		try {
			out = response.getWriter();
			out.println("<html>");
			out.println("<head><title>Ooops</title></head>");
			out.println("something went wrong...<br>");
			out.println(msg);
			out.println("</BODY>");
			out.println("</HTML>");
			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
