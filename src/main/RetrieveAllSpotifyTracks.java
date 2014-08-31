/**
 * Author: Fredrik Hagfjäll
 * Connecting to Spotify to get all the tracks with the same name as from the sverigesradio.se website. 
 */
package main;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DateTimeException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.Locale;
import java.util.TreeMap;
import java.util.logging.Logger;

import com.wrapper.spotify.exceptions.WebApiException;
import com.wrapper.spotify.models.Track;

public class RetrieveAllSpotifyTracks {

	private String chosenDateLong, chosenDate;
	private final String playlistFile;
	private String dateFormatShort = "yyyy-MM-dd",
			dateFormatLong = "EEEE d MMMM yyyy";
	private final String inputUrl;
	private final TreeMap<Song, Track> songOnSpotify = new TreeMap<Song, Track>();
	private Logger logger;

	/**
	 * 
	 * @param input
	 *            - the url for the site to get the tracknames from
	 * @throws ParseException
	 *             - couldn't get any date from the <b>input</b>
	 * @throws InputMismatchException
	 *             - Wrong input
	 */
	public RetrieveAllSpotifyTracks(String input)
			throws InputMismatchException, ParseException {
		inputUrl = input;
		chosenDate = Tools.getDateFromURL(inputUrl);
		playlistFile = input.substring(input.indexOf("latlista.aspx?")
				+ "latlista.aspx?".length());
		DateFormat dfShort = new SimpleDateFormat(dateFormatShort, new Locale(
				"sv"));
		DateFormat dfLong = new SimpleDateFormat(dateFormatLong, new Locale(
				"sv"));
		chosenDateLong = dfLong.format(dfShort.parse(chosenDate));
		logger = Logger.getLogger(RetrieveAllSpotifyTracks.class.getName()
				+ " " + chosenDate);

	}

	public void start() {
		logger.finer("Starting to download the tracks for " + inputUrl);
		if (cacheExists())
			return;
		try {
			getSpotifyTracks();
		} catch (IOException | WebApiException e) {
			e.printStackTrace();
		}
		Tools.IOtoDisk(true, Integer.toString(playlistFile.hashCode()),
				songOnSpotify);
	}

	private boolean cacheExists() {
		return new File(Settings.cachePath, Integer.toString(playlistFile
				.hashCode())).exists();
	}

	private void getSpotifyTracks() throws IOException, WebApiException {
		URL url = new URL(inputUrl);
		HashMap<String, ArrayList<Song>> dateSongMap = ParseHTML
				.extractDateAndSongs(url);
		String key = "";
		for (String dateKey : dateSongMap.keySet()) {
			String date = dateKey.toLowerCase();
			if (date.contains(chosenDateLong.toLowerCase())) {
				key = dateKey;
				break;
			}
		}
		if (!dateSongMap.containsKey(key)) {
			throw new DateTimeException("Chosen date not valid");
		}
		logger.fine("downloading Spotify-data");
		for (Song song : dateSongMap.get(key)) {
			Track spotifyTrack = GetSpotifyTrack.findTrack(song);
			if (spotifyTrack == null) {
				logger.finer("No match!\n" + song + "\n\t"
						+ Tools.prntTrack(spotifyTrack));
			}
			songOnSpotify.put(song, spotifyTrack);
		}
		for (Song song : songOnSpotify.keySet()) {
			logger.finest(song + "\n\t"
					+ Tools.prntTrack(songOnSpotify.get(song)));
		}
	}

}
