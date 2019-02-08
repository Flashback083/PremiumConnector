package eu.horyzon.premiumconnector.tasks;

import java.sql.SQLException;

import eu.horyzon.premiumconnector.PremiumConnector;
import eu.horyzon.premiumconnector.objects.MojangPlayer;
import eu.horyzon.premiumconnector.objects.UserData;
import eu.horyzon.premiumconnector.utils.MojangAPI;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.event.PreLoginEvent;

public class PremiumCheck implements Runnable {
	private final PremiumConnector plugin;
	private final PreLoginEvent e;

	public PremiumCheck(PremiumConnector plugin, PreLoginEvent e) {
		this.plugin = plugin;
		this.e = e;
	}

	@Override
	public void run() {
		PendingConnection conn = e.getConnection();
		String name = conn.getName();

		if (plugin.getConnections().containsKey(name)) {
			conn.disconnect(plugin.getParser().parse(plugin.getMessages().notPremiumError));
		} else {
			try {
				String ip = conn.getAddress().getAddress().getHostAddress();
				UserData data = null;
				plugin.getLogger().info("Try to connect " + name + " to the server!");

				try {
					data = UserData.loadFromName(name);
					plugin.getLogger().info("Data successfull load from SQL");
				} catch (NullPointerException npe) {
					try {
						MojangPlayer mp = MojangAPI.getMojangPlayer(name);
						plugin.getLogger().info("Data successfull load from Mojang's servers");
						try {
							data = UserData.loadFromUUID(mp.getId());
						} catch (NullPointerException npe1) {
							data = new UserData(conn, mp.getId());
						}
					} catch (NullPointerException npe1) {
						data = new UserData(conn, null);
					}
				} finally {
					if (data != null) {
						conn.setOnlineMode(data.isPremium());
						if (!data.isPremium())
							conn.setUniqueId(data.getUUID());
						data.setName(name);
						data.setAuthentificated(data.isPremium());
						data.setIp(ip);
						data.update();
					}

					plugin.getConnections().put(name, data);
				}
			} catch (SQLException e) {
				conn.disconnect(plugin.getParser().parse(plugin.getMessages().mySQLError));
			} catch (Exception e) {
				conn.disconnect(plugin.getParser().parse(plugin.getMessages().mojangServerError));
				plugin.getLogger().warning("Error with Mojang's servers");
			}
		}

		e.completeIntent(plugin);
	}
}