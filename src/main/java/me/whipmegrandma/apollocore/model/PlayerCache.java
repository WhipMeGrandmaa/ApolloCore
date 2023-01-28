package me.whipmegrandma.apollocore.model;

import lombok.Data;
import lombok.Getter;
import me.whipmegrandma.apollocore.manager.EnchantsManager;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.collection.SerializedMap;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Data
@Getter
public class PlayerCache {

	private static Map<UUID, PlayerCache> playerCache = new HashMap<>();

	private UUID uuid;
	private String username;
	private Integer tokens;
	private Map<Enchantment, Integer> enchantments;
	private Rank rank;
	private Integer blocksBroken;

	private PlayerCache(UUID uuid, String username, Integer tokens, Map<Enchantment, Integer> enchantments, Rank rank, Integer blocksBroken) {
		this.uuid = uuid;
		this.username = username;
		this.tokens = tokens;
		this.enchantments = enchantments;
		this.rank = rank;
		this.blocksBroken = blocksBroken;
	}

	public void setTokens(Integer tokens) {
		this.tokens = tokens;
	}

	public void setEnchantment(Enchantment enchantment, Integer level) {
		this.enchantments.put(enchantment, level);
	}

	public Integer getEnchantLevel(Enchantment enchantment) {
		return this.enchantments.getOrDefault(enchantment, 0);
	}

	public void upgradeRank() {
		this.rank = rank.getNextRank();
	}

	public void addToCache() {
		playerCache.put(this.uuid, this);
	}

	public void increaseBlocksBroken() {
		blocksBroken++;
	}

	public void setBlocksBroken(Integer blocksBroken) {
		this.blocksBroken = blocksBroken;
	}

	public void addBlocksBroken(Integer blocksBroken) {
		this.blocksBroken += blocksBroken;
	}

	public void removeFromCache() {
		playerCache.remove(this.uuid);
	}

	public SerializedMap enchantmentsToMap() {
		SerializedMap map = new SerializedMap();

		for (Map.Entry<Enchantment, Integer> entry : this.enchantments.entrySet()) {
			map.put(entry.getKey().getKey().getKey(), entry.getValue());
		}

		return map;
	}

	public static PlayerCache from(Player player) {
		UUID uuid = player.getUniqueId();
		String username = player.getName();
		PlayerCache cache = playerCache.get(uuid);

		if (cache == null)
			cache = new PlayerCache(uuid, username, 0, new HashMap<>(), Rank.getFirstRank() != null ? Rank.getFirstRank() : null, 0);

		return cache;
	}

	public static PlayerCache fromDatabase(ResultSet resultSet) {

		try {
			UUID uuid = UUID.fromString(resultSet.getString("UUID"));
			String username = resultSet.getString("Username");
			Integer tokens = resultSet.getInt("Tokens");
			Map<Enchantment, Integer> enchants = deserializeEnchants(SerializedMap.fromJson(resultSet.getString("Enchantments")));
			Rank rank = Rank.getByName(resultSet.getString("Rank")) != null ? Rank.getByName(resultSet.getString("Rank")) : Rank.getFirstRank();
			Integer blocksBroken = resultSet.getInt("Blocks_Broken");

			return new PlayerCache(uuid, username, tokens, enchants, rank, blocksBroken);
		} catch (Throwable t) {
			Common.error(t, "Unable to convert data from database.");
		}

		return null;
	}

	private static Map<Enchantment, Integer> deserializeEnchants(SerializedMap map) {
		Map<Enchantment, Integer> enchants = new HashMap<>();

		for (Map.Entry<String, Object> entry : map.entrySet()) {
			Enchantment enchantment = EnchantsManager.getByName(entry.getKey());
			Integer level = ((BigDecimal) entry.getValue()).intValue();

			enchants.put(enchantment, level);
		}

		return enchants;
	}

}
