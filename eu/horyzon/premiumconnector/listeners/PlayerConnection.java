package eu.horyzon.premiumconnector.listeners;

import eu.horyzon.premiumconnector.PremiumConnector;
import eu.horyzon.premiumconnector.tasks.PremiumCheck;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PreLoginEvent;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.event.ServerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

public class PlayerConnection implements Listener {
	private PremiumConnector plugin;

	public PlayerConnection(PremiumConnector plugin) {
		this.plugin = plugin;
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onPreLogin(PreLoginEvent e) {
		if (e.isCancelled())
			return;

		e.registerIntent(plugin);

		plugin.getProxy().getScheduler().runAsync(plugin, new PremiumCheck(plugin, e));
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onServerConnect(ServerConnectEvent e) {
		ProxiedPlayer p = e.getPlayer();
		if (!plugin.getConnections().containsKey(p.getName()))
			return;

		String redirect = plugin.getConnections().get(p.getName()).isAuthentificated()
				? (p.getPendingConnection().getVirtualHost() != null
						? p.getPendingConnection().getVirtualHost().getHostName() : "default")
				: "crack";

		if (!redirect.equals(e.getTarget().getName()))
			e.setTarget(PremiumConnector.getServer(redirect));

		plugin.getConnections().remove(p.getName());
	}

	public void onServerDisconnect(ServerDisconnectEvent e) {
		plugin.getConnections().remove(e.getPlayer().getName());
	}
}