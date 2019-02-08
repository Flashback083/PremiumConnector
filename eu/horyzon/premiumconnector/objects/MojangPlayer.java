package eu.horyzon.premiumconnector.objects;

import java.util.UUID;

public class MojangPlayer {
	private String id;
	private String name;

	public UUID getId() {
		return UUID.fromString(id.substring(0, 8) + "-" + id.substring(8, 12) + "-" + id.substring(12, 16) + "-"
				+ id.substring(16, 20) + "-" + id.substring(20, 32));
	}

	public String getName() {
		return name;
	}
}