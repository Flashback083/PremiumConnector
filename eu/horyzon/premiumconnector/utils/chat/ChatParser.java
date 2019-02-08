package eu.horyzon.premiumconnector.utils.chat;

import net.md_5.bungee.api.chat.BaseComponent;

public abstract interface ChatParser {

	public abstract BaseComponent[] parse(String paramString);

}