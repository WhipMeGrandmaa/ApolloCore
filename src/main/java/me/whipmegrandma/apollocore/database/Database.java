package me.whipmegrandma.apollocore.database;

import lombok.Getter;
import me.whipmegrandma.apollocore.model.PlayerCache;
import me.whipmegrandma.apollocore.model.Rank;
import org.bukkit.entity.Player;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.Valid;
import org.mineacademy.fo.collection.SerializedMap;
import org.mineacademy.fo.database.SimpleDatabase;

import java.sql.ResultSet;
import java.util.function.Consumer;

public class Database extends SimpleDatabase {
	@Getter
	private static Database instance = new Database();

	private Database() {
		this.addVariable("table", "ApolloCore");
	}

	@Override
	protected void onConnected() {
		this.createTable(TableCreator.of("{table}")
				.addNotNull("UUID", "VARCHAR(64)")
				.add("Username", "TEXT")
				.add("Tokens", "INTEGER")
				.add("Enchantments", "LONGTEXT")
				.add("Rank", "LONGTEXT")
				.setPrimaryColumn("UUID"));
	}

	public void load(Player player, Consumer<PlayerCache> callback) {
		this.checkLoadedAndSync();

		Common.runAsync(() -> {

			try {

				synchronized (this) {
					ResultSet resultSet = this.query("SELECT * FROM {table} WHERE UUID='" + player.getUniqueId() + "'");

					if (!resultSet.next()) {
						Common.runLater(() -> callback.accept(null));

						return;
					}

					Common.runLater(() -> callback.accept(PlayerCache.fromDatabase(resultSet)));
				}
			} catch (Throwable t) {
				Common.error(t, "Unable to load player data for " + player.getName());
			}
		});
	}

	public void load(String username, Consumer<PlayerCache> callback) {
		this.checkLoadedAndSync();

		Common.runAsync(() -> {

			try {

				synchronized (this) {
					ResultSet resultSet = this.query("SELECT * FROM {table} WHERE Username='" + username + "' COLLATE NOCASE");

					if (!resultSet.next()) {
						Common.runLater(() -> callback.accept(null));

						return;
					}

					Common.runLater(() -> callback.accept(PlayerCache.fromDatabase(resultSet)));
				}
			} catch (Throwable t) {
				Common.error(t, "Unable to load player data for " + username);
			}
		});
	}

	public void save(Player player, Consumer<PlayerCache> callback) {
		this.checkLoadedAndSync();

		Common.runAsync(() -> {

			PlayerCache cache = PlayerCache.from(player);

			try {

				SerializedMap map = SerializedMap.ofArray(
						"UUID", player.getUniqueId(),
						"Username", player.getName(),
						"Tokens", cache.getTokens(),
						"Enchantments", cache.enchantmentsToMap().toJson(),
						"Rank", cache.getRank() != null ? cache.getRank().getName() : Rank.getFirstRank() != null ? Rank.getFirstRank().getName() : "No Rank");

				final String columns = Common.join(map.keySet());
				final String values = Common.join(map.values(), ", ", value -> value == null || value.equals("NULL") ? "NULL" : "'" + value + "'");

				synchronized (this) {
					this.update("INSERT OR REPLACE INTO {table} (" + columns + ") VALUES (" + values + ");");
				}

				Common.runLater(() -> callback.accept(cache));

			} catch (Throwable t) {
				Common.error(t, "Unable to save data for player.");
			}
		});
	}

	public void update(String username, String column, Object updatedValue) {
		this.checkLoadedAndSync();

		Common.runAsync(() -> {

			try {

				synchronized (this) {
					this.update("UPDATE {table} set " + column + " = '" + updatedValue + "' WHERE Username = '" + username + "' COLLATE NOCASE");
				}

			} catch (Throwable t) {
				Common.error(t, "Unable to update " + column + " to " + updatedValue + " for " + username);
			}
		});
	}

	private void checkLoadedAndSync() {
		Valid.checkSync("Database calls must happen sync, not async!");

		if (!this.isConnected())
			this.connectUsingLastCredentials();

		Valid.checkBoolean(this.isConnected(), "Not connected to database.");

	}
}
