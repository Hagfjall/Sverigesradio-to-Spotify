package main;

public class Settings {
	public static final String urlToSommariP1 = "http://sverigesradio.se/sida/latlista.aspx?programid=2071&date=";
	public static final String clientId = "<Client ID to Spotify API";
	public static final String clientSecret = "<Client secret to spotify API>";
	public static final String redirectURI = "<RedirectURL>";
	public static double matchingThreshold = 0.62;
	public static final String delimiter = "_|_";
	public static final String cachePath = "/opt/tomcat/webapps/sommarip1/cache/";
	public static final String spotifyScope[] = { "playlist-modify-public",
			"user-read-private", "user-library-modify","playlist-read-private" };

}
