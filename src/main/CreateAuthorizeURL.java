package main;

import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import com.wrapper.spotify.Api;

public class CreateAuthorizeURL {

	public static void send(String url, PrintWriter out) {
		final String clientId = Settings.clientId;
		final String clientSecret = Settings.clientSecret;
		final String redirectURI = Settings.redirectURI;

		final Api api = Api.builder().clientId(clientId)
				.clientSecret(clientSecret).redirectURI(redirectURI).build();

		// TODO ta reda på rätt koder
		final List<String> scopes = Arrays.asList(Settings.spotifyScope);
		String state = url.substring(url.indexOf("latlista.aspx?")
				+ "latlista.aspx?".length());
		state = Integer.toString(state.hashCode());
		String authorizeURL = api.createAuthorizeURL(scopes, state);
		out.println(authorizeURL);
		Logger logger = Logger.getLogger(CreateAuthorizeURL.class.getName());
		logger.finer("Printwriter: " + out + " authurl: " + authorizeURL);
	}

}
