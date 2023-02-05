package me.whipmegrandma.apollocore.model;

import de.slikey.effectlib.effect.SphereEffect;
import lombok.Getter;
import me.whipmegrandma.apollocore.hook.EffectLibHook;
import me.whipmegrandma.apollocore.hook.VaultHook;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.entity.Player;
import org.mineacademy.fo.ReflectionUtil;
import org.mineacademy.fo.Valid;
import org.mineacademy.fo.collection.SerializedMap;
import org.mineacademy.fo.model.Triple;
import org.mineacademy.fo.remain.CompSound;
import org.mineacademy.fo.settings.ConfigItems;
import org.mineacademy.fo.settings.YamlConfig;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Getter
public class Rank extends YamlConfig {

	private static ConfigItems<Rank> ranks = ConfigItems.fromFile("", "ranks.yml", Rank.class);
	private static Rank firstRank;

	private String rankFormatted;
	private String nextRank;
	private Integer upgradePrice;
	private UpgradeType upgradeType;
	private Boolean infiniteRanks;
	private Integer infiniteNumber;
	private Double upgradePriceMultiplier;
	private SerializedMap upgradeByLevel;

	private Rank(String name) {
		this.setPathPrefix(name);

		this.loadConfiguration(NO_DEFAULT, "ranks.yml");
	}

	private Rank(String rankFormatted, String nextRank, Integer upgradePrice, UpgradeType upgradeType, Boolean infiniteRanks, Integer infiniteNumber, Double upgradePriceMultiplier, SerializedMap upgradeByLevel) {
		this.rankFormatted = rankFormatted;
		this.nextRank = nextRank;
		this.upgradePrice = upgradePrice;
		this.upgradeType = upgradeType;
		this.infiniteRanks = infiniteRanks;
		this.infiniteNumber = infiniteNumber;
		this.upgradePriceMultiplier = upgradePriceMultiplier;
		this.upgradeByLevel = upgradeByLevel;
	}

	@Override
	protected void onLoad() {
		this.rankFormatted = getString("Rank_Formatted", this.getPathPrefix());
		this.nextRank = getString("Next_Rank", null);
		this.upgradePrice = getInteger("Upgrade_Price", null);
		this.upgradeType = ReflectionUtil.lookupEnumSilent(UpgradeType.class, getString("Upgrade_Type", "Upgrade").toUpperCase());
		this.infiniteRanks = getBoolean("Infinite_Ranks", false);
		this.infiniteNumber = 1;
		this.upgradePriceMultiplier = getDouble("Infinite_Upgrade_Price_Multiplier", 1.0);
		this.upgradeByLevel = getMap("Infinite_Price_By_Level");

		if (getBoolean("First_Rank", false))
			firstRank = this;
	}

	public UpgradeResult upgrade(Player player) {
		UpgradeResult result = this.canUpgrade(player);

		if (result == UpgradeResult.SUCCESS) {
			ApolloPlayer cache = ApolloPlayer.from(player);

			cache.upgradeRank();
			this.onUpgrade(player);
		}

		return result;
	}

	public UpgradeResult canUpgrade(Player player) {
		if (!this.infiniteRanks && (this.nextRank == null || this.upgradePrice == null))
			return UpgradeResult.MAX_RANK;

		Economy economy = VaultHook.getEconomy();
		double balance = economy.getBalance(player);

		if (balance >= this.getUpgradePrice()) {
			economy.withdrawPlayer(player, this.getUpgradePrice());

			return UpgradeResult.SUCCESS;
		}

		return UpgradeResult.INSUFFICIENT_BALANCE;
	}

	public double getUpgradePrice() {
		if (this.upgradePrice == null && this.upgradeByLevel.isEmpty())
			return 0;

		for (Map.Entry<String, Object> entry : this.upgradeByLevel.entrySet()) {
			int prestige = Valid.isInteger(entry.getKey()) ? Integer.parseInt(entry.getKey()) : 0;
			int price = Valid.isInteger(entry.getValue().toString()) ? Integer.parseInt(entry.getValue().toString()) : 0;

			if (this.getInfiniteNumber() + 1 == prestige)
				return price;
		}

		double multiplier = this.upgradePriceMultiplier;

		for (int i = 1; i < this.getInfiniteNumber(); i++)
			multiplier = multiplier * this.upgradePriceMultiplier;

		DecimalFormat decimal = new DecimalFormat("0.00");

		return Double.parseDouble(decimal.format(this.upgradePrice * multiplier));
	}

	public String getRankFormatted() {
		if (!this.getInfiniteRanks())
			return this.rankFormatted;

		return this.rankFormatted.replace("%number%", this.getInfiniteNumber().toString());
	}

	public Rank getNextRank() {
		if (!this.getInfiniteRanks())
			return getByName(this.nextRank);

		return this.getInfiniteRank(this.getInfiniteNumber() + 1);
	}

	public Rank getInfiniteRank(Integer infiniteNumber) {
		Rank rank = new Rank(this.rankFormatted, this.nextRank, this.upgradePrice, this.upgradeType, this.infiniteRanks, infiniteNumber, this.upgradePriceMultiplier, this.upgradeByLevel);
		rank.setPathPrefix(this.getPathPrefix());

		return rank;
	}

	private void onUpgrade(Player player) {

		SphereEffect effect = new de.slikey.effectlib.effect.SphereEffect(EffectLibHook.getEffectManager());

		effect.setEntity(player);
		effect.setTargetPlayer(player);
		effect.radius = 2;
		effect.particleCount = 1;
		effect.period = 2;
		effect.iterations = 10;

		effect.start();

		CompSound.ENTITY_ENDER_DRAGON_GROWL.play(player);
	}

	@Override
	public String getName() {
		if (!this.infiniteRanks)
			return this.getPathPrefix();

		return this.getPathPrefix() + this.getInfiniteNumber();
	}

	public static Rank getFirstRank() {
		return firstRank;
	}

	public static Rank getByName(String name) {
		Rank byName = ranks.findItem(name);

		if (byName != null)
			return byName;

		for (Rank rank : getRanks())
			if (rank.getPathPrefix().equalsIgnoreCase(name.replaceAll("[0-9]", ""))) {
				int number = Integer.parseInt(name.replaceAll("[^0-9.]", ""));

				return rank.getInfiniteRank(number);
			}

		return null;
	}

	public static Triple<UpgradeResult, Double, Integer> upgradeMax(Player player, UpgradeType type) {
		ApolloPlayer cache = ApolloPlayer.from(player);
		Rank rank = cache.getRank();
		Economy economy = VaultHook.getEconomy();

		double spent = 0;
		int times = 0;

		UpgradeResult result = UpgradeResult.INSUFFICIENT_BALANCE;

		while (economy.getBalance(player) >= rank.getUpgradePrice() + spent) {
			if (rank.getNextRank() == null)
				return new Triple<>(UpgradeResult.MAX_RANK, spent, times);

			if (rank.getUpgradeType() != type)
				break;

			spent += rank.getUpgradePrice();
			rank = rank.getNextRank();

			times++;

			result = UpgradeResult.SUCCESS;
		}

		if (result == UpgradeResult.SUCCESS) {
			cache.setRank(rank);
			rank.onUpgrade(player);

			economy.withdrawPlayer(player, spent);
		}

		return new Triple<>(result, spent, times);
	}

	public static List<Rank> getRanks() {
		return ranks.getItems();
	}

	public static Set<String> getRankNames() {
		return ranks.getItemNames();
	}

	public static void loadRanks() {
		ranks.loadItems();
	}

	public enum UpgradeType {
		RANKUP,
		PRESTIGE
	}

	public enum UpgradeResult {
		INSUFFICIENT_BALANCE,
		MAX_RANK,
		SUCCESS

	}
}
