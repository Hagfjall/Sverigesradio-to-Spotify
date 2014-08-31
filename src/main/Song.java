/**
 * Author: Fredrik Hagfjäll
 * 
 * Contains the artists and the name of the Song. Simple as that, no album, tracknumber or anything like that. 
 */
package main;

import java.util.ArrayList;
import java.util.List;

public class Song implements Comparable<Song> {

	private static long counter;
	private long id;
	private String name;
	private List<String> artists;

	public Song(String info) {
		int index = info.indexOf("-");
		String allArtists = info.substring(0, index);
		artists = extractArtist(allArtists);
		name = info.substring(index + 1);
		name = name.trim();
		id = counter++;
	}

	private List<String> extractArtist(String allArtists) {
		List<String> ret = new ArrayList<String>();
		int index = 0, nextIndex = 0;
		if (allArtists.indexOf(",") == -1) {
			ret.add(allArtists.trim());
			return ret;
		}
		while (allArtists.indexOf(",", index) != -1) {
			nextIndex = allArtists.indexOf(",", index);
			String artist = allArtists.substring(index, nextIndex);
			index = nextIndex + 1;
			artist = artist.trim();
			ret.add(artist);
		}
		ret.add(allArtists.substring(index).trim());
		return ret;

	}

	/**
	 * 
	 * @return the original list of artists, no one's going to change that on
	 *         the go, right?
	 */
	public List<String> getArtists() {
		return artists;
	}

	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (String artist : artists) {
			sb.append(artist);
			sb.append(", ");
		}
		sb.delete(sb.length() - 2, sb.length());
		sb.append(" - ");
		sb.append(name);
		return sb.toString();
	}

	@Override
	public int compareTo(Song song) {
		return Long.compare(id, song.id);
	}
}
