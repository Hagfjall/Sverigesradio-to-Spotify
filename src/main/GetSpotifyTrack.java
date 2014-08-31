package main;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.logging.Logger;

import com.wrapper.spotify.Api;
import com.wrapper.spotify.exceptions.WebApiException;
import com.wrapper.spotify.methods.TrackSearchRequest;
import com.wrapper.spotify.models.SimpleArtist;
import com.wrapper.spotify.models.Track;

public class GetSpotifyTrack {
	private static double threshold = Settings.threshold;
	private static Api api;
	private static Logger logger;
	static {
		api = Api.builder().clientId(Settings.clientId)
				.clientSecret(Settings.clientSecret)
				.redirectURI(Settings.redirectURI).build();
		logger = Logger.getLogger(GetSpotifyTrack.class.getName());
	}

	public static Track findTrack(Song song) throws IOException,
			WebApiException {
		List<Track> trackSearchResult = new ArrayList<Track>();
		for (String artist : song.getArtists()) {
			final TrackSearchRequest request = api
					.searchTracks(
							"track:" + song.getName() + " artist:" + artist)
					.limit(50).build();
			logger.fine("query: " + request.toStringWithQueryParameters());
			trackSearchResult.addAll(request.get().getItems());
		}
		TreeMap<Double, Track> searchResult = new TreeMap<Double, Track>();
		for (Track track : trackSearchResult) {
			double srchDist = getLevenshteinDist(track, song);
			searchResult.put(srchDist, track);
		}
		if (searchResult.size() > 0 && searchResult.lastKey() >= threshold) {
			logger.fine("Song: " + song + "\nHits on Spotify:");
			for (double key : searchResult.keySet()) {
				logger.fine(key + Tools.prntTrack(searchResult.get(key)));
			}
			return searchResult.get(searchResult.lastKey());
		} else
			return null;
	}

	private static double getLevenshteinDist(Track track, Song song) {
		double result = 0;
		int counter = 0;
		for (String songArtist : song.getArtists()) {
			for (SimpleArtist trackArtist : track.getArtists()) {
				counter++;

				String spotifyTrack = trackArtist.getName() + track.getName();
				String searchedSong = songArtist + song.getName();
				spotifyTrack = removeJunkFromString(spotifyTrack);
				searchedSong = removeJunkFromString(searchedSong);
				double levenshtienDistance = org.apache.commons.lang.StringUtils
						.getLevenshteinDistance(spotifyTrack, searchedSong);
				logger.finest("comparing '" + spotifyTrack + "' with '"
						+ searchedSong + "'");
				double prcntMatched = (spotifyTrack.length() - levenshtienDistance)
						/ spotifyTrack.length();
				if (sameArtist(songArtist, track.getArtists()))
					result *= 1.2;
				result += prcntMatched;
			}
		}
		return result / counter;
	}

	private static boolean sameArtist(String artist,
			List<SimpleArtist> artistsList) {
		for (int i = 0; i < artistsList.size(); i++) {
			artist = removeJunkFromString(artist);
			String trackArtist = removeJunkFromString(artistsList.get(i)
					.getName());
			if (artist.equals(trackArtist))
				return true;
		}
		return false;
	}

	private static String removeJunkFromString(String input) {
		String output = input;
		output = output.replaceAll("\\s+|[-(),&/?!:.]", "");
		output = output.toUpperCase();
		return convertNonAscii(output);
	}

	private static String convertNonAscii(String s) {
		final String UNICODE = "\u00C0\u00E0\u00C8\u00E8\u00CC\u00EC\u00D2\u00F2\u00D9\u00F9"
				+ "\u00C1\u00E1\u00C9\u00E9\u00CD\u00ED\u00D3\u00F3\u00DA\u00FA\u00DD\u00FD"
				+ "\u00C2\u00E2\u00CA\u00EA\u00CE\u00EE\u00D4\u00F4\u00DB\u00FB\u0176\u0177"
				+ "\u00C3\u00E3\u00D5\u00F5\u00D1\u00F1"
				+ "\u00C4\u00E4\u00CB\u00EB\u00CF\u00EF\u00D6\u00F6\u00DC\u00FC\u0178\u00FF"
				+ "\u00C5\u00E5" + "\u00C7\u00E7" + "\u0150\u0151\u0170\u0171";
		final String PLAIN_ASCII = "AaEeIiOoUu" // grave
				+ "AaEeIiOoUuYy" // acute
				+ "AaEeIiOoUuYy" // circumflex
				+ "AaOoNn" // tilde
				+ "AaEeIiOoUuYy" // umlaut
				+ "Aa" // ring
				+ "Cc" // cedilla
				+ "OoUu" // double acute
		;
		StringBuilder sb = new StringBuilder(s.length() + 1);
		int n = s.length();
		for (int i = 0; i < n; i++) {
			char c = s.charAt(i);
			int pos = UNICODE.indexOf(c);
			if (pos > -1) {
				sb.append(PLAIN_ASCII.charAt(pos));
			} else {
				sb.append(c);
			}
		}
		return sb.toString();
	}
}
