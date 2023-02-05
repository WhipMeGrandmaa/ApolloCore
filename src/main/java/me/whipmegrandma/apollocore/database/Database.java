package me.whipmegrandma.apollocore.database;

import lombok.Getter;
import me.whipmegrandma.apollocore.api.IntermediateDatabase;
import me.whipmegrandma.apollocore.model.ApolloPlayer;
import me.whipmegrandma.apollocore.model.Rank;
import org.bukkit.entity.Player;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.Valid;
import org.mineacademy.fo.collection.SerializedMap;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.function.Consumer;

public class Database extends IntermediateDatabase {
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
				.add("Blocks_Broken", "INTEGER")
				.add("Player_Shop", "LONGTEXT")
				.setPrimaryColumn("UUID"));
	}

	public void load(Player player, Consumer<ApolloPlayer> callback) {
		this.checkLoadedAndSync();

		Common.runAsync(() -> {

			try {

				synchronized (this) {
					ResultSet resultSet = this.query("SELECT * FROM {table} WHERE UUID='" + player.getUniqueId() + "'");

					if (!resultSet.next()) {
						Common.runLater(() -> callback.accept(null));

						return;
					}

					Common.runLater(() -> callback.accept(ApolloPlayer.fromDatabase(resultSet)));
				}
			} catch (Throwable t) {
				Common.error(t, "Unable to load player data for " + player.getName());
			}
		});
	}

	public void load(String username, Consumer<ApolloPlayer> callback) {
		this.checkLoadedAndSync();

		Common.runAsync(() -> {

			try {

				synchronized (this) {
					ResultSet resultSet = this.query("SELECT * FROM {table} WHERE Username='" + username + "' COLLATE NOCASE");

					if (!resultSet.next()) {
						Common.runLater(() -> callback.accept(null));

						return;
					}

					Common.runLater(() -> callback.accept(ApolloPlayer.fromDatabase(resultSet)));
				}
			} catch (Throwable t) {
				Common.error(t, "Unable to load player data for " + username);
			}
		});
	}

	public void loadAll(Consumer<List<ApolloPlayer>> callback) {
		this.checkLoadedAndSync();

		Common.runAsync(() -> {
			List<ApolloPlayer> dataList = new ArrayList<>();

			this.selectAll("{table}", data -> dataList.add(ApolloPlayer.fromDatabase(data)));

			for (ListIterator<ApolloPlayer> iterator = dataList.listIterator(); iterator.hasNext(); ) {
				ApolloPlayer offlinePlayer = iterator.next();

				if (ApolloPlayer.contains(offlinePlayer.getUuid())) {
					iterator.remove();
					iterator.add(ApolloPlayer.from(offlinePlayer.getUuid()));
				}
			}

			Common.runLater(() -> callback.accept(dataList));
		});
	}

	public void save(Player player, Consumer<ApolloPlayer> callback) {
		this.checkLoadedAndSync();

		Common.runAsync(() -> {

			ApolloPlayer cache = ApolloPlayer.from(player);

			try {

				SerializedMap map = SerializedMap.ofArray(
						"UUID", player.getUniqueId(),
						"Username", player.getName(),
						"Tokens", cache.getTokens(),
						"Enchantments", cache.enchantmentsToMap().toJson(),
						"Rank", cache.getRank() != null ? cache.getRank().getName() : Rank.getFirstRank() != null ? Rank.getFirstRank().getName() : "No Rank",
						"Blocks_Broken", cache.getBlocksBroken(),
						"Player_Shop", cache.shopItemsToMap().toJson().replace("'", "''"));

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

	public void update(String username, String column, Object updatedValue, Consumer<String> callback) {
		this.checkLoadedAndSync();

		Common.runAsync(() -> {

			try {

				synchronized (this) {
					this.update("UPDATE {table} set " + column + " = '" + updatedValue + "' WHERE Username = '" + username + "' COLLATE NOCASE");
				}

				Common.runLater(() -> callback.accept(username));

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
