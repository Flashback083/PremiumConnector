package eu.horyzon.premiumconnector.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.Files;

import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

public class MessageUtil {
	public String mySQLError, notPremiumError, mojangServerError;

	public MessageUtil(Plugin plugin) {
		File file = new File(plugin.getDataFolder(), "message.yml");
		try {
			if (!file.exists())
				Files.copy(plugin.getResourceAsStream("message.yml"), file.toPath(), new CopyOption[0]);

			Configuration message = ConfigurationProvider.getProvider(YamlConfiguration.class).load(file);

			mySQLError = message.getString("MySQLError");
			notPremiumError = message.getString("NotPremiumError");
			mojangServerError = message.getString("MojangServerError");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}