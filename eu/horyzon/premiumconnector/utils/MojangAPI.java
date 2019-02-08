package eu.horyzon.premiumconnector.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import eu.horyzon.premiumconnector.objects.MojangPlayer;
import net.md_5.bungee.BungeeCord;

public class MojangAPI {
	public static MojangPlayer getMojangPlayer(String playerName) throws Exception, NullPointerException {
		HttpsURLConnection connection = getConnection("https://api.mojang.com/users/profiles/minecraft/" + playerName);
		if (connection.getResponseCode() == 200) {
			BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String line = reader.readLine();
			if (!line.equals("null"))
				return parseMojangPlayer(line);
			else
				throw new NullPointerException();
		} else if (connection.getResponseCode() == 204)
			throw new NullPointerException();
		else
			throw new Exception();
	}

	private static MojangPlayer parseMojangPlayer(String json) {
		return ((MojangPlayer) BungeeCord.getInstance().gson.fromJson(json, MojangPlayer.class));
	}

	private static HttpsURLConnection getConnection(String url) throws IOException {
		HttpsURLConnection connection = (HttpsURLConnection) new URL(url).openConnection();
		connection.setConnectTimeout(3000);
		connection.setReadTimeout(6000);

		connection.setRequestProperty("Content-Type", "application/json");
		connection.setRequestProperty("User-Agent", "Premium-Checker");
		return connection;
	}
}