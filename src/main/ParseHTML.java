package main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParseHTML {

	private static final Pattern datePattern;

	/**
	 * 
	 * @param url
	 *            - the website to parse (must be http://sverigesradio.se...)
	 * @return HashMap with <String(Date in format EEEE d MMMM yyyy, in
	 *         swedish), List<Song>>
	 */
	public static HashMap<String, ArrayList<Song>> extractDateAndSongs(URL url) {
		String src = getSource(url);
		src = Tools.replaceHtmlEntities(src);

		Pattern dateSourcePattern = Pattern
				.compile("<h3 id=\"([0-9]+)\" class=\"day th-color label label-heading\">(.*?)<li id=");
		Matcher dateMatch = dateSourcePattern.matcher(src);
		Pattern songSourcePattern = Pattern
				.compile("<span class=\"track-title\">(.*?)</span>");
		HashMap<String, ArrayList<Song>> ret = new HashMap<String, ArrayList<Song>>(
				26);
		while (dateMatch.find()) {
			String sourceForOneDay = dateMatch.group(2);
			String date = getDate(sourceForOneDay);
			Matcher songMatch = songSourcePattern.matcher(sourceForOneDay);
			ArrayList<Song> songsForOneDay = new ArrayList<Song>();
			while (songMatch.find()) {
				String song = songMatch.group(1);
				song = Tools.replaceHtmlEntities(song);
				songsForOneDay.add(new Song(song));
			}
			ret.put(date, songsForOneDay);
		}
		return ret;
	}

	static {
		datePattern = Pattern.compile("<a href=\"(.*?)\">(.*?)</a>");
	}

	/**
	 * 
	 * @param src
	 * @return date in format EEEE d MMMM yyyy
	 */
	public static String getDate(String src) {
		Matcher m = datePattern.matcher(src);
		m.find();
		String dateLink = m.group(2);
		if (dateLink.indexOf('-') != -1) { // removes any explanation of the
											// certain day on sverigesradio.se
			dateLink = dateLink.substring(0, dateLink.indexOf('-'));
		}
		return dateLink.trim();
	}

	private static String getSource(URL url) {
		InputStream is = null;
		BufferedReader reader = null;
		try {
			URLConnection conn = url.openConnection();
			conn.connect();
			String content = conn.getContentType();
			String charset = content.substring(content.indexOf("charset=")
					+ "charset=".length());
			is = conn.getInputStream();
			if (charset.equalsIgnoreCase("UTF-8"))
				reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
			else
				reader = new BufferedReader(new InputStreamReader(is));
			String nextline;
			StringBuilder sb = new StringBuilder();
			while ((nextline = reader.readLine()) != null) {
				sb.append(nextline);
			}
			return org.apache.commons.lang.StringEscapeUtils.unescapeHtml(sb
					.toString());
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
}
