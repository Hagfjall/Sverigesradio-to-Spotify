package main;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import com.wrapper.spotify.Api;
import com.wrapper.spotify.exceptions.WebApiException;
import com.wrapper.spotify.methods.AddTrackToPlaylistRequest;
import com.wrapper.spotify.methods.CurrentUserRequest;
import com.wrapper.spotify.methods.PlaylistCreationRequest;
import com.wrapper.spotify.models.AuthorizationCodeCredentials;
import com.wrapper.spotify.models.Playlist;
import com.wrapper.spotify.models.User;

public class CreatePlaylist {
	private Api api;
	private User user;
	private Logger logger;

	public CreatePlaylist(String callback) {
		logger = Logger.getLogger(CreatePlaylist.class.getName());
		run(callback);
	}

	public void run(String callback) {
		int codeIndex = callback.indexOf("?code=");
		int stateIndex = callback.indexOf("&state=");
		String code = callback.substring(codeIndex + 6, stateIndex);
		logger.finer("code: " + code);
		api = Api.builder().clientId(Settings.clientId)
				.clientSecret(Settings.clientSecret)
				.redirectURI(Settings.redirectURI).build();
		try {
			AuthorizationCodeCredentials auth = api
					.authorizationCodeGrant(code).build().get();
			logger.finer("token: " + auth.getAccessToken());
			api.setAccessToken(auth.getAccessToken());
			api.setRefreshToken(auth.getRefreshToken());
			logger.finer("trying to get user...");
			CurrentUserRequest request = api.getMe().build();
			user = request.get();
			logger.finer("User uri: " + user.getId());
		} catch (IOException | WebApiException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void printName(PrintWriter out) {
		if (user.getDisplayName().equals("null"))
			out.println(user.getId());
		else
			out.println(user.getDisplayName());
	}

	public void createPlaylist(List<String> uris) {
		Iterator<String> it = uris.iterator();
		while (it.hasNext()) {
			String uri = it.next();
			if (uri.equals("null"))
				it.remove();
		}
		logger.fine("Got these URIs:");
		for (String uri : uris) {
			logger.fine(uri);
		}
		PlaylistCreationRequest request = api
				.createPlaylist(user.getId(),
						"Sverigesradio.se playlist generated by Hagfjall.se")
				.publicAccess(true).build();
		try {
			Playlist playlist = request.get();
			logger.finer("created playlist with id " + playlist.getId());
			addSongs(playlist, uris);
		} catch (IOException | WebApiException e) {
			e.printStackTrace();
		}

	}

	private void addSongs(Playlist playlist, List<String> uris)
			throws IOException, WebApiException {
		logger.fine("trying to add track to playlist id: " + playlist.getId() + " on user: " + user.getId());
		final AddTrackToPlaylistRequest request = api
				.addTracksToPlaylist(user.getId(), playlist.getId(), uris)
				.position(0).build();

		request.get(); // Empty response
		logger.fine("added songs?!");
	}
}
