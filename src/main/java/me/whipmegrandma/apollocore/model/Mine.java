package me.whipmegrandma.apollocore.model;

import com.github.zandy.playerborderapi.api.PlayerBorderAPI;
import lombok.Data;
import me.whipmegrandma.apollocore.enums.Operator;
import me.whipmegrandma.apollocore.manager.MineWorldManager;
import me.whipmegrandma.apollocore.settings.MineSettings;
import me.whipmegrandma.apollocore.util.WorldEditUtil;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.collection.SerializedMap;
import org.mineacademy.fo.model.ConfigSerializable;
import org.mineacademy.fo.region.Region;
import org.mineacademy.fo.remain.CompMaterial;
import org.mineacademy.fo.remain.Remain;

import java.io.File;
import java.util.List;
import java.util.UUID;

@Data
public class Mine implements ConfigSerializable {

	private Location location;
	private Location home;
	private Location center;
	private Region mineRegion;
	private Boolean canTeleport;
	private List<UUID> allowedPlayers;

	private void pasteSchematic(Location location, File schematic) {
		WorldEditUtil.paste(location, schematic);
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

	public void delete() {
		Region region = getRegion();

		if (region == null)
			return;

		List<Block> blocks = region.getBlocks();

		for (Block block : blocks)
			Remain.setTypeAndData(block, CompMaterial.AIR);
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

	public void teleport(Player player) {
		this.teleport(player, this.home, true);
	}

	public void teleport(Player player, Location location, boolean displayBorder) {
		Chunk chunk = location.getChunk();

		if (!chunk.isLoaded())
			chunk.load();

		player.teleport(location);

		if (displayBorder)
			this.showBorder(player);
	}

	public void showBorder(Player player) {
		Location center = this.center != null ? this.center : this.location;

		Common.runLater(5, () -> PlayerBorderAPI.getInstance().setBorder(player, PlayerBorderAPI.BorderColor.BLUE, MineSettings.getInstance().getBorderRadius() * 2, center.getBlockX(), center.getBlockZ()));
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

	public static Mine create() {
		return create(MineWorldManager.getNextFreeLocation(), true);
	}

	public static Mine create(Location nextFreeLocation, boolean isPlayer) {
		Mine mine = new Mine();
		Mine defaultMine = MineSettings.getInstance().getDefaultMine();
		File schematic = MineSettings.getInstance().getSchematic();

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

			if (isPlayer)
				mine.canTeleport = true;

			mine.mineRegion = new Region(primaryMine, secondaryMine);
		}

		return mine;
	}

	public static boolean isWithinRegion(Location location) {
		for (ApolloPlayer player : ApolloPlayer.getAllCached())
			if (player.getMine() != null && player.getMine().isWithin(location))
				return true;

		return false;
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
