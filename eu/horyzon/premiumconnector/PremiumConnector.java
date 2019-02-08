package eu.horyzon.premiumconnector;

import com.huskehhh.mysql.Database;
import com.huskehhh.mysql.mysql.MySQL;
import eu.horyzon.premiumconnector.listeners.PlayerConnection;
import eu.horyzon.premiumconnector.listeners.PlayerPrevent;
import eu.horyzon.premiumconnector.objects.UserData;
import eu.horyzon.premiumconnector.utils.MessageUtil;
import eu.horyzon.premiumconnector.utils.chat.BBCodeChatParser;
import eu.horyzon.premiumconnector.utils.chat.ChatParser;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

public class PremiumConnector extends Plugin {
	private static PremiumConnector instance;
	private static MessageUtil messages;
	private static Map<String, String> forcedHost;
	private Map<String, UserData> connections = new HashMap<String, UserData>();
	private static Database sql;
	private static ChatParser chatParser = new BBCodeChatParser();

	public void onEnable() {
		instance = this;
		Logger log = getLogger();

		if (!getDataFolder().exists())
			getDataFolder().mkdir();

		File file = new File(getDataFolder(), "config.yml");

		if (!file.exists())
			try (InputStream in = getResourceAsStream("config.yml")) {
				Files.copy(in, file.toPath());
			} catch (IOException e) {
				e.printStackTrace();
			}
		messages = new MessageUtil(this);

		Configuration config = null;
		try {
			config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(file);
		} catch (IOException e) {
			e.printStackTrace();
		}

		// INITIALIZE MySQL
		if (config.getBoolean("mysql.enable")) {
			String host = config.getString("mysql.host");
			String port = config.getString("mysql.port");
			String database = config.getString("mysql.database");
			String username = config.getString("mysql.username");
			String password = config.getString("mysql.password");
			sql = new MySQL(host, port, database, username, password);
		} else {
			log.warning("Please configure mysql in config.");
			getProxy().stop();
		}

		// INITIALIZE CONNECTION
		try {
			sql.openConnection();
			sql.updateSQL(
					"CREATE TABLE IF NOT EXISTS PremiumConnector (UUID CHARACTER(36) NOT NULL, Name VARCHAR(26) NOT NULL, LastIp VARCHAR(255) NOT NULL, Premium BOOLEAN, PRIMARY KEY (UUID));");
		} catch (SQLException e) {
			getLogger().warning(ChatColor.RED + "Impossible to connect to the database: " + e.getMessage());
			getProxy().stop();
		}

		// REGISTER FORCED HOST
		forcedHost = new HashMap<String, String>();

		for (String server : config.getSection("forced_hosts").getKeys())
			for (String host : config.getStringList("forced_hosts." + server))
				forcedHost.put(host, server);

		getProxy().getPluginManager().registerListener(this, new PlayerPrevent(forcedHost.get("crack")));
		getProxy().getPluginManager().registerListener(this, new PlayerConnection(this));
	}

	public static PremiumConnector getInstance() {
		return instance;
	}

	public static Database getSQL() {
		return sql;
	}

	public Map<String, UserData> getConnections() {
		return connections;
	}

	public static ServerInfo getServer(String host) {
		return getInstance().getProxy()
				.getServerInfo(forcedHost.containsKey(host) ? forcedHost.get(host) : forcedHost.get("default"));
	}

	public MessageUtil getMessages() {
		return messages;
	}

	public ChatParser getParser() {
		return chatParser;
	}
}
