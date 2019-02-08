package eu.horyzon.premiumconnector.listeners;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;

public class PlayerPrevent implements Listener {
	private String crackedServer;

	public PlayerPrevent(String crackedServer) {
		this.crackedServer = crackedServer;
	}

	public void onChat(ChatEvent e) {
		if (!(e.getSender() instanceof ProxiedPlayer))
			return;

		ProxiedPlayer p = (ProxiedPlayer) e.getSender();
		if (p.getServer().getInfo().getName().equals(crackedServer)) {
			p.sendMessage(new ComponentBuilder("Please login you before do something!").color(ChatColor.RED).create());
			e.setCancelled(true);
		}
	}
}