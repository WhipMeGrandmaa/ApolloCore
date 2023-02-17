package me.whipmegrandma.apollocore.model;

import lombok.Data;
import org.mineacademy.fo.ReflectionUtil;
import org.mineacademy.fo.collection.SerializedMap;
import org.mineacademy.fo.model.ConfigSerializable;
import org.mineacademy.fo.model.SimpleSound;
import org.mineacademy.fo.remain.CompMaterial;
import org.mineacademy.fo.remain.CompParticle;

@Data
public class EnchantByLevelSettings implements ConfigSerializable {

	private Double chance;
	private Integer minimumTokens;
	private Integer maximumTokens;
	private Double blocksIncome;
	private Double multiplier;
	private Integer craterRadius;

	private CompMaterial materialDroplet;
	private Integer spawnHeightDroplet;
	private Integer craterRadiusDroplet;
	private Double velocityDroplet;
	private CompParticle particleDroplet;
	private Double particleDropletChance;

	private SimpleSound soundOnBreak;
	private CompParticle particleOnBreak;
	private Boolean lightningOnBreak;

	private SimpleSound soundOnImpact;

	private CompParticle particleBrokenBlocks;
	private Double particleBrokenBlocksChance;
	private Double velocityTowardsBlackhole;

	private SimpleSound soundOnBlackholeDisappear;

	@Override
	public SerializedMap serialize() {
		return null;
	}

	public static EnchantByLevelSettings deserialize(SerializedMap map) {
		EnchantByLevelSettings settings = new EnchantByLevelSettings();

		settings.chance = map.containsKey("Chance") ? map.getDouble("Chance") : 1.0;
		settings.minimumTokens = map.containsKey("Minimum_Tokens") ? map.getInteger("Minimum_Tokens") : 5;
		settings.maximumTokens = map.containsKey("Maximum_Tokens") ? map.getInteger("Maximum_Tokens") : 20;
		settings.blocksIncome = map.containsKey("Blocks_Income") ? map.getDouble("Blocks_Income") : 0.7;
		settings.multiplier = map.containsKey("Multiplier") ? map.getDouble("Multiplier") : 1.5;
		settings.craterRadius = map.containsKey("Crater_Radius") ? map.getInteger("Crater_Radius") : 5;

		SerializedMap dropletMap = SerializedMap.of(map.getMap("Droplet"));
		settings.materialDroplet = dropletMap.containsKey("Material") ? dropletMap.getMaterial("Material") : CompMaterial.NETHER_STAR;
		settings.spawnHeightDroplet = dropletMap.containsKey("Spawn_Height_Above_Block") ? dropletMap.getInteger("Spawn_Height_Above_Block") : 10;
		settings.craterRadiusDroplet = dropletMap.containsKey("Crater_Radius") ? dropletMap.getInteger("Crater_Radius") : 5;
		settings.velocityDroplet = dropletMap.containsKey("Velocity") ? dropletMap.getDouble("Velocity") : 3.0;
		settings.particleDroplet = dropletMap.containsKey("Particle") ? ReflectionUtil.lookupEnumSilent(CompParticle.class, dropletMap.getString("Particle").toUpperCase()) : null;
		settings.particleDropletChance = dropletMap.containsKey("Particle_Chance") ? dropletMap.getDouble("Particle_Chance") : 1.0;

		SerializedMap onBreakMap = SerializedMap.of(map.getMap("On_Break"));
		settings.soundOnBreak = onBreakMap.containsKey("Sound") ? onBreakMap.get("Sound", SimpleSound.class) : null;
		settings.particleOnBreak = onBreakMap.containsKey("Particle") ? ReflectionUtil.lookupEnumSilent(CompParticle.class, onBreakMap.getString("Particle").toUpperCase()) : null;
		settings.lightningOnBreak = onBreakMap.getBoolean("Lightning");

		SerializedMap onImpactMap = SerializedMap.of(map.getMap("On_Impact"));
		settings.soundOnImpact = onImpactMap.containsKey("Sound") ? onImpactMap.get("Sound", SimpleSound.class) : null;

		SerializedMap brokenBlocksMap = SerializedMap.of(map.getMap("Broken_Blocks"));
		settings.particleBrokenBlocks = brokenBlocksMap.containsKey("Particle") ? ReflectionUtil.lookupEnumSilent(CompParticle.class, brokenBlocksMap.getString("Particle").toUpperCase()) : null;
		settings.particleBrokenBlocksChance = brokenBlocksMap.containsKey("Particle_Chance") ? brokenBlocksMap.getDouble("Particle_Chance") : 1.0;
		settings.velocityTowardsBlackhole = brokenBlocksMap.containsKey("Velocity_Towards_Blackhole") ? brokenBlocksMap.getDouble("Velocity_Towards_Blackhole") : 1.0;

		SerializedMap onBlackholeMap = SerializedMap.of(map.getMap("On_Blackhole_Disappear"));
		settings.soundOnBlackholeDisappear = onBlackholeMap.containsKey("Sound") ? onBlackholeMap.get("Sound", SimpleSound.class) : null;

		return settings;
	}
}