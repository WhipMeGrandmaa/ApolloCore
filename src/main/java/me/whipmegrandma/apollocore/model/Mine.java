package me.whipmegrandma.apollocore.model;

import com.github.zandy.playerborderapi.api.PlayerBorderAPI;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import lombok.Data;
import me.whipmegrandma.apollocore.enums.Operator;
import me.whipmegrandma.apollocore.manager.MineWorldManager;
import me.whipmegrandma.apollocore.settings.MineSettings;
import me.whipmegrandma.apollocore.util.WorldEditUtil;
import me.whipmegrandma.apollocore.util.WorldGuardUtil;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.collection.SerializedMap;
import org.mineacademy.fo.model.ConfigSerializable;
import org.mineacademy.fo.model.Tuple;
import org.mineacademy.fo.region.Region;
import org.mineacademy.fo.remain.CompMaterial;
import org.mineacademy.fo.remain.Remain;

import java.text.DecimalFormat;
import java.util.*;

@Data
public class Mine implements ConfigSerializable {

	private ApolloPlayer owner;
	private Location location;
	private Location home;
	private Location center;
	private Region mineRegion;
	private Boolean canTeleport;
	private List<UUID> allowedPlayers;

	public Mine() {
		this.allowedPlayers = new ArrayList<>();
	}

	private void pasteSchematic(Location location, Clipboard clipboard) {
		WorldEditUtil.paste(location, clipboard);
	}

	@Override
	public SerializedMap serialize() {
		SerializedMap map = new SerializedMap();

		if (this.location != null)
			map.put("Location", this.location);

		if (this.home != null)
			map.put("Home", this.home);

		if (this.center != null)
			map.put("Center", this.center);

		if (this.mineRegion != null)
			map.put("Mine_Region", this.mineRegion);

		if (this.canTeleport != null)
			map.put("Can_Teleport", this.canTeleport);

		if (this.allowedPlayers != null && !this.allowedPlayers.isEmpty())
			map.put("Allowed_Players", this.allowedPlayers);

		return map;
	}

	public boolean isWithin(Location location) {
		return this.getRegion() != null && this.getRegion().isWithin(location);
	}

	public boolean isWithinMine(Location location) {
		return this.getMineRegion().isWithin(location);
	}

	public void delete() {
		for (Player player : Remain.getOnlinePlayers())
			if (this.isWithin(player.getLocation()))
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "/spawn " + player.getName());

		Region region = getRegion();

		if (region == null)
			return;

		WorldEditUtil.setBlocks(this.getRegion(), CompMaterial.AIR);

		WorldGuardUtil.deleteMineRegion(this.owner, mineRegion);
	}

	public Region getRegion() {
		if (this.center == null)
			return null;

		World world = location.getWorld();
		int borderRadius = MineSettings.getInstance().getBorderRadius();

		Location primary = this.getCenter().clone().add(borderRadius, world.getMaxHeight(), borderRadius);
		Location secondary = this.getCenter().clone().add(-borderRadius, world.getMinHeight(), -borderRadius);

		return new Region(primary, secondary);
	}

	public Region getMineRegion() {
		if (this.mineRegion == null)
			return null;

		if (owner == null)
			return this.mineRegion;

		int increaseDiameter = this.owner.getRank().getMineRegionIncrease();
		Location primary = this.mineRegion.getPrimary();
		Location secondary = this.mineRegion.getSecondary();

		if (primary.getBlockX() > secondary.getBlockX()) {
			primary.add(increaseDiameter, 0, 0);
			secondary.add(-increaseDiameter, 0, 0);
		} else if (secondary.getBlockX() > primary.getBlockX()) {
			secondary.add(increaseDiameter, 0, 0);
			primary.add(-increaseDiameter, 0, 0);
		}

		if (primary.getBlockZ() > secondary.getBlockZ()) {
			primary.add(0, 0, increaseDiameter);
			secondary.add(0, 0, -increaseDiameter);
		} else if (secondary.getBlockZ() > primary.getBlockZ()) {
			secondary.add(0, 0, increaseDiameter);
			primary.add(0, 0, -increaseDiameter);
		}

		return new Region(primary, secondary);
	}

	public Tuple<Integer, Integer> getDimensionsMine() {
		return getDimensionsMine(this.owner != null ? this.owner.getRank() : null);
	}

	public static Tuple<Integer, Integer> getDimensionsMine(Rank rank) {
		Mine defaultMine = MineSettings.getInstance().getDefaultMine();

		if (defaultMine == null)
			return new Tuple<>(0, 0);

		int primaryX = defaultMine.getMineRegion().getPrimary().getBlockX();
		int secondaryX = defaultMine.getMineRegion().getSecondary().getBlockX();
		int primaryZ = defaultMine.getMineRegion().getPrimary().getBlockZ();
		int secondaryZ = defaultMine.getMineRegion().getSecondary().getBlockZ();

		int x = Math.abs(primaryX - secondaryX);
		int z = Math.abs(primaryZ - secondaryZ);

		if (rank != null) {
			int increaseDiameter = rank.getMineRegionIncrease();
			x += increaseDiameter;
			z += increaseDiameter;
		}

		return new Tuple<>(x, z);
	}

	public List<Tuple<CompMaterial, Double>> getMineBlocks() {
		return getMineBlocks(this.owner != null ? this.owner.getRank() : null);
	}

	public static List<Tuple<CompMaterial, Double>> getMineBlocks(Rank rank) {
		if (rank == null)
			return null;

		HashMap<CompMaterial, Double> mineBlocks = rank.getMineBlockChances();
		List<Tuple<CompMaterial, Double>> list = new ArrayList<>();

		for (Map.Entry<CompMaterial, Double> entry : mineBlocks.entrySet()) {
			DecimalFormat decimalFormat = new DecimalFormat("0.00");
			Double chance = Double.valueOf(decimalFormat.format(entry.getValue()));

			list.add(new Tuple<>(entry.getKey(), chance));
		}

		return list;
	}

	public int getAllowedPlayersAmount() {
		return this.allowedPlayers.size();
	}

	public int getAmountAllowedPlayersOnline() {
		int amount = 0;

		for (UUID uuid : this.allowedPlayers) {
			Player player = Remain.getPlayerByUUID(uuid);

			if (player != null)
				amount++;
		}

		Player player = Remain.getPlayerByUUID(this.owner.getUuid());

		if (player != null)
			amount++;

		return amount;
	}

	public boolean isPlayerAllowed(Player player) {
		return this.isPlayerAllowed(player.getUniqueId()) || player.getUniqueId().equals(this.owner.getUuid());
	}

	public boolean isPlayerAllowed(UUID uuid) {
		return this.allowedPlayers.contains(uuid) || uuid.equals(this.owner.getUuid());
	}

	public void addAllowedPlayer(Player player) {
		this.allowedPlayers.add(player.getUniqueId());

		WorldGuardUtil.addMemberToMineRegion(this.owner, player.getUniqueId());
	}

	public void removeAllowedPlayer(Player player) {
		if (player != null && this.isWithin(player.getLocation()))
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "/spawn " + player.getName());

		this.allowedPlayers.remove(player.getUniqueId());

		WorldGuardUtil.kickMemberFromMineRegion(this.owner, player.getUniqueId());
	}

	public void removeAllowedPlayer(UUID uuid) {
		this.allowedPlayers.remove(uuid);
	}

	public List<String> getAllowedPlayerNames() {
		List<String> names = new ArrayList<>();

		for (UUID uuid : this.getAllowedPlayers()) {
			OfflinePlayer player = Remain.getOfflinePlayerByUUID(uuid);

			if (player != null)
				names.add(player.getName());
		}

		return names;
	}

	public void resetMine() {
		HashMap<CompMaterial, Double> mineBlocks = this.owner.getRank().getMineBlockChances();

		WorldEditUtil.setBlocks(this.mineRegion, mineBlocks);

		for (Player player : Remain.getOnlinePlayers())
			if (this.isWithinMine(player.getLocation())) {
				this.teleportToHome(player);

				if (!owner.getUuid().equals(player.getUniqueId()))
					Common.tell(player, "The mine of " + this.owner.getUsername() + " has been reset.");
			}
	}

	public void teleportToHome(Player player) {
		this.teleportToHome(player, this.home, true);
	}

	public void teleportToHome(Player player, Location location, boolean displayBorder) {
		Chunk chunk = location.getChunk();

		if (!chunk.isLoaded())
			chunk.load();

		player.teleport(location);

		if (displayBorder)
			this.showBorder(player);
	}

	public void showBorder(Player player) {
		Location center = this.center != null ? this.center : this.location;

		Common.runLater(2, () -> PlayerBorderAPI.getInstance().setBorder(player, PlayerBorderAPI.BorderColor.BLUE, MineSettings.getInstance().getBorderRadius() * 2, center.getBlockX(), center.getBlockZ()));
	}

	public static Mine deserialize(SerializedMap map) {
		Mine mine = new Mine();

		mine.location = map.get("Location", Location.class);
		mine.home = map.get("Home", Location.class);
		mine.center = map.get("Center", Location.class);
		mine.mineRegion = map.get("Mine_Region", Region.class);
		mine.canTeleport = map.getBoolean("Can_Teleport");
		mine.allowedPlayers = map.getList("Allowed_Players", UUID.class);

		return mine;
	}

	public static Mine create(Location nextFreeLocation) {
		return create(null, nextFreeLocation);
	}

	public static Mine create(ApolloPlayer owner) {
		return create(owner, MineWorldManager.getNextFreeLocation());
	}

	public static Mine create(ApolloPlayer owner, Location nextFreeLocation) {
		Mine mine = new Mine();
		Mine defaultMine = MineSettings.getInstance().getDefaultMine();
		Clipboard schematic = MineSettings.getInstance().getSchematic();

		if (owner != null)
			mine.owner = owner;

		mine.location = nextFreeLocation;

		if (schematic == null)
			return mine;

		mine.pasteSchematic(nextFreeLocation, schematic);

		Location adjustedLocation = nextFreeLocation.clone();
		adjustedLocation.setY(0);

		if (defaultMine.getHome() != null)
			mine.home = defaultMine.getHome().clone().add(adjustedLocation);

		if (defaultMine.getCenter() != null)
			mine.center = defaultMine.getCenter().clone().add(adjustedLocation);

		if (defaultMine.getMineRegion() != null) {
			Location primaryMine = defaultMine.getMineRegion().getPrimary().clone().add(adjustedLocation);
			Location secondaryMine = defaultMine.getMineRegion().getSecondary().clone().add(adjustedLocation);

			mine.mineRegion = new Region(primaryMine, secondaryMine);

			mine.resetMine();
		}

		if (owner != null) {
			mine.owner = owner;
			mine.canTeleport = true;

			WorldGuardUtil.createMineRegion(owner, mine.mineRegion);
		}

		return mine;
	}

	public static Mine getWithinMineRegion(Location location) {
		for (ApolloPlayer player : ApolloPlayer.getAllCached())
			if (player.getMine() != null && player.getMine().isWithinMine(location))
				return player.getMine();

		return null;
	}

	public static Mine getWithinRegion(Location location) {
		for (ApolloPlayer player : ApolloPlayer.getAllCached())
			if (player.getMine() != null && player.getMine().isWithin(location))
				return player.getMine();

		return null;
	}

	public static boolean isLocationTaken(Location location) {
		for (ApolloPlayer player : ApolloPlayer.getAllCached())
			if (player.getMine() != null && player.getMine().getLocation().equals(location))
				return true;

		return false;
	}

	public static void updateAll() {
		for (ApolloPlayer player : ApolloPlayer.getAllCached()) {
			Mine mine = player.getMine();

			if (mine == null)
				return;

			Mine defaultMine = MineSettings.getInstance().getDefaultMine();

			if (defaultMine.getHome() != null)
				mine.setHome(MineWorldManager.transform(defaultMine.getHome(), mine.getLocation(), Operator.PLUS));

			if (defaultMine.getCenter() != null)
				mine.setCenter(MineWorldManager.transform(defaultMine.getCenter(), mine.getLocation(), Operator.PLUS));

			if (defaultMine.getMineRegion() != null)
				mine.setMineRegion(MineWorldManager.transform(defaultMine.getMineRegion(), mine.getLocation(), Operator.PLUS));
		}
	}
}
