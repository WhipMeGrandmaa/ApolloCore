package me.whipmegrandma.apollocore.settings;

import me.whipmegrandma.apollocore.database.Database;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.FileUtil;
import org.mineacademy.fo.settings.YamlStaticConfig;


public class Settings extends YamlStaticConfig {

	public static String host;
	public static Integer port;
	public static String database;
	public static String username;
	public static String password;

	@Override
	protected void onLoad() throws Exception {
		this.loadConfiguration("settings.yml");
	}

	private static void init() {
		setPathPrefix("MySQL");

		host = isSet("Host") ? getString("Host") : null;
		port = isSet("Port") ? getInteger("Port") : null;
		database = isSet("Database") ? getString("Database") : null;
		username = isSet("Username") ? getString("Username") : null;
		password = isSet("Password") ? getString("Password") : null;

		if (!host.equals("null") && port != -1 && !database.equals("null") && !username.equals("null") && !password.equals("null")) {
			Database.getInstance().connect(host, port, database, username, password);

			return;
		}

		Common.log("[ApolloCore] MySQL is not set in settings.yml, connecting to SQLite.");
		Database.getInstance().connect("jdbc:sqlite:" + FileUtil.getOrMakeFile("database.sqlite").getAbsolutePath());

	}
}
