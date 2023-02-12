package me.whipmegrandma.apollocore.manager;

import lombok.Getter;
import me.whipmegrandma.apollocore.command.mine.MineEditSubCommand;
import me.whipmegrandma.apollocore.enums.Operator;
import me.whipmegrandma.apollocore.model.Mine;
import me.whipmegrandma.apollocore.settings.MineSettings;
import me.whipmegrandma.apollocore.util.DirectionUtil;
import org.bukkit.*;
import org.bukkit.block.Biome;
import org.bukkit.generator.BiomeProvider;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.generator.WorldInfo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mineacademy.fo.MathUtil;
import org.mineacademy.fo.model.Tuple;
import org.mineacademy.fo.region.Region;
import org.mineacademy.fo.visual.VisualizedRegion;

import java.util.Arrays;
import java.util.List;

public class MineWorldManager {
	@Getter
	private static World mineWorld;
	private static Location defaultLocation;
	@Getter
	private static DirectionUtil direction;

	public static void load() {
		mineWorld = createWorld();
		defaultLocation = new Location(mineWorld, 0, 128, 0);
		direction = DirectionUtil.NORTH;
	}

	private static World createWorld() {

		World mineWorld = Bukkit.createWorld(
				new WorldCreator(MineSettings.getInstance().getWorldName())
						.type(WorldType.FLAT)
						.environment(MineSettings.getInstance().getEnvironment())
						.generator(new ChunkGenerator() {

							@Override
							public boolean shouldGenerateNoise() {
								return false;
							}

							@Nullable
							@Override
							public BiomeProvider getDefaultBiomeProvider(@NotNull WorldInfo worldInfo) {
								return new BiomeProvider() {
									@NotNull
									@Override
									public Biome getBiome(@NotNull WorldInfo worldInfo, int i, int i1, int i2) {
										return MineSettings.getInstance().getBiome();
									}

									@NotNull
									@Override
									public List<Biome> getBiomes(@NotNull WorldInfo worldInfo) {
										return Arrays.asList(MineSettings.getInstance().getBiome());
									}
								};
							}
						}));

		return mineWorld;
	}

	public static synchronized Location getNextFreeLocation() {
		return getNextFreeLocationData().getKey();
	}

	public static synchronized Tuple<Location, Integer> getNextFreeLocationData() {
		int shell = 0;
		int times = 0;

		Location location = defaultLocation;
		DirectionUtil direction = getDirection();

		while (Mine.isLocationTaken(location) || location.equals(MineEditSubCommand.getInstance().getNextFreeLocation())) {
			if (direction == DirectionUtil.NORTH)
				++shell;

			location = direction.transform(defaultLocation, shell * MineSettings.getInstance().getDistanceBetweenMines());
			direction = direction.next();

			++times;
		}

		return new Tuple<>(location, times);
	}

	public static Region transform(VisualizedRegion region, Location nextFreeLocation, Operator operator) {
		return transform(new Region(region.getPrimary(), region.getSecondary()), nextFreeLocation, operator);
	}

	public static Region transform(Region region, Location nextFreeLocation, Operator operator) {
		Location primary = transform(region.getPrimary(), nextFreeLocation, operator);
		Location secondary = transform(region.getSecondary(), nextFreeLocation, operator);

		return new Region(primary, secondary);
	}

	public static synchronized Location transform(Location location, Location nextFreeLocation, Operator operator) {
		String operatorString = operator.toString();

		World world = location.getWorld();
		int x = (int) MathUtil.calculate(location.getBlockX() + operatorString + nextFreeLocation.getBlockX());
		int y = location.getBlockY();
		int z = (int) MathUtil.calculate(location.getBlockZ() + operatorString + nextFreeLocation.getBlockZ());

		Location newLocation = new Location(world, x, y, z);
		newLocation.setPitch(location.getPitch());
		newLocation.setYaw(location.getYaw());

		return newLocation;
	}
}
