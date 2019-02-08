package eu.horyzon.premiumconnector.objects;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import com.google.common.base.Charsets;

import eu.horyzon.premiumconnector.PremiumConnector;
import net.md_5.bungee.api.connection.PendingConnection;

public class UserData {
	protected UUID id;
	protected String name;
	protected String ip;
	protected boolean premium = false;
	protected boolean authentificated = false;

	public UserData(UUID id, String name, String ip, boolean premium) {
		this.id = id;
		this.name = name;
		this.ip = ip;
		this.premium = premium;
	}

	public UserData(PendingConnection conn, UUID id) throws SQLException {
		this(id == null ? generateUUID(conn.getName()) : id, conn.getName(),
				"0.0.0.0", id != null);
		insert();
	}

	public static UserData loadFromName(String name) throws NullPointerException, SQLException {
		return loadData(name, "Name");
	}

	public static UserData loadFromUUID(UUID id) throws NullPointerException, SQLException {
		return loadData(id.toString(), "UUID");
	}

	public static UserData loadData(String object, String key) throws SQLException, NullPointerException {
		ResultSet rs = PremiumConnector.getSQL().querySQL(String.format("SELECT * FROM PremiumConnector WHERE %s='%s'", key, object));

		if (rs.next())
			return new UserData(UUID.fromString(rs.getString("UUID")), rs.getString("Name"), rs.getString("LastIp"),
					rs.getBoolean("premium"));

		throw new NullPointerException();
	}

	public void insert() throws SQLException {
		PremiumConnector.getSQL().updateSQL(String.format("INSERT INTO PremiumConnector (UUID, Name, LastIp, Premium) VALUES ('%s', '%s', '%s', '%d')", id.toString(), name, ip, premium ? 1 : 0));
	}

	public void update() throws SQLException {
		PremiumConnector.getSQL().updateSQL(String.format("UPDATE PremiumConnector SET Name='%s', LastIp='%s', Premium='%d' WHERE UUID='%s'", name, ip, premium ? 1 : 0, id.toString()));
	}

	public static UUID generateUUID(String name) {
		return UUID.nameUUIDFromBytes(("OfflinePlayer:" + name).getBytes(Charsets.UTF_8));
	}

	public UUID getUUID() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getIp() {
		return ip;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setPremium(boolean premium) {
		this.premium = premium;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public void setAuthentificated(boolean authentificated) {
		this.authentificated = authentificated;
	}

	public boolean isPremium() {
		return premium;
	}

	public boolean isAuthentificated() {
		return authentificated;
	}
}