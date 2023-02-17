package me.whipmegrandma.apollocore.settings;

import lombok.Getter;
import me.whipmegrandma.apollocore.ApolloCore;
import me.whipmegrandma.apollocore.model.Mine;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.mineacademy.fo.FileUtil;
import org.mineacademy.fo.ReflectionUtil;
import org.mineacademy.fo.region.Region;
import org.mineacademy.fo.settings.YamlConfig;

import java.io.File;

@Getter
public class MineSettings extends YamlConfig {
	@Getter
	private static MineSettings instance;

	private String worldName;
	private Integer distanceBetweenMines;
	private World.Environment environment;
	private Biome biome;
	private File schematic;
	private Mine defaultMine;
	private Integer borderRadius;
	private Integer maxMinePlayerSize;
	private Integer resetMineSeconds;
	private Integer resetMineReminderSeconds;

	private MineSettings() {
		this.loadConfiguration(NO_DEFAULT, "minesettings.yml");
	}

	@Override
	protected void onLoad() {
		this.worldName = isSet("World_Name") ? getString("World_Name") : "world_mines";
		this.distanceBetweenMines = isSet("Distance_Between_Mines") ? getInteger("Distance_Between_Mines") : 100;
		this.environment = isSet("World_Type") ? ReflectionUtil.lookupEnumSilent(World.Environment.class, getString("World_Type")) : World.Environment.NORMAL;
		this.biome = isSet("Biome") ? ReflectionUtil.lookupEnumSilent(Biome.class, getString("Biome")) : Biome.PLAINS;

		FileUtil.createIfNotExists("schematics/");
		this.schematic = FileUtil.getFile("schematics/mine.schematic");

		this.borderRadius = isSet("Border_Radius") ? getInteger("Border_Radius") : 20;
		this.defaultMine = isSet("Default_Mine") && Bukkit.getWorld(this.worldName) != null ? get("Default_Mine", Mine.class) : new Mine();
		this.maxMinePlayerSize = isSet("Max_Mine_Player_Size") ? getInteger("Max_Mine_Player_Size") : 3;
		this.resetMineSeconds = isSet("Reset_All_Mines_Seconds") ? getInteger("Reset_All_Mines_Seconds") : 60;
		this.resetMineReminderSeconds = isSet("Reset_Mine_Reminder_Every_Seconds") ? getInteger("Reset_Mine_Reminder_Every_Seconds") : 30;
	}

	@Override
	protected void onSave() {
		set("Default_Mine", this.defaultMine);
		set("Border_Radius", this.borderRadius);
	}

	public void setDefaultMineRegion(Region mineRegion) {
		this.defaultMine.setMineRegion(mineRegion);

		this.save();
	}

	public void setDefaultHomeLocation(Location location) {
		this.defaultMine.setHome(location);

		this.save();
	}

	public void setDefaultCenter(Location location) {
		this.defaultMine.setCenter(location);

		this.save();
	}

	public void setRadius(Integer radius) {
		this.borderRadius = radius;

		this.save();
	}

	public static void load() {
		ApolloCore apolloCore = ApolloCore.getInstance();

		if (!new File(apolloCore.getDataFolder(), "minesettings.yml").exists())
			ApolloCore.getInstance().saveResource("minesettings.yml", false);

		instance = new MineSettings();
	}
}
