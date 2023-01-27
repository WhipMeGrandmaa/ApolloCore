package me.whipmegrandma.apollocore.model;

import lombok.Data;
import org.bukkit.entity.EntityType;
import org.mineacademy.fo.ReflectionUtil;
import org.mineacademy.fo.model.SimpleSound;
import org.mineacademy.fo.remain.CompMaterial;
import org.mineacademy.fo.remain.CompParticle;
import org.mineacademy.fo.settings.ConfigItems;
import org.mineacademy.fo.settings.YamlConfig;

import java.util.List;
import java.util.Set;

@Data
public class MineBomb extends YamlConfig {

	private static ConfigItems<MineBomb> bombs = ConfigItems.fromFile("", "minebombs.yml", MineBomb.class);

	private String name;
	private List<String> lore;
	private CompMaterial material;
	private Boolean glow;
	private Integer radius;
	private Double throwVelocity;
	private SimpleSound throwSound;
	private EntityType flyingEntity;
	private CompMaterial flyingMaterial;
	private SimpleSound flyingParticleSound;
	private CompParticle flyingParticle;
	private Double flyingParticleChance;
	private SimpleSound explosionParticleSound;
	private CompParticle explosionParticle;
	private Double explosionParticleChance;

	private MineBomb(String name) {
		this.setPathPrefix(name);

		this.loadConfiguration("minebombs.yml");
	}

	@Override
	protected void onLoad() {
		this.name = this.getString("Name");
		this.lore = this.getStringList("Lore");
		this.material = this.getMaterial("Material", CompMaterial.ENDER_EYE);
		this.glow = this.getBoolean("Glow", false);
		this.radius = this.getInteger("Radius", 5);

		this.throwVelocity = this.getDouble("Throw.Velocity", 3.0);
		this.throwSound = this.getSound("Throw.Sound", null);

		this.flyingEntity = this.isSet("Flying.Entity") ? ReflectionUtil.lookupEnumSilent(EntityType.class, this.getString("Flying.Entity").toUpperCase()) : null;
		this.flyingMaterial = this.flyingEntity == null ? this.getMaterial("Flying.Material", null) : null;
		this.flyingParticleSound = this.getSound("Flying.Sound", null);
		this.flyingParticle = this.isSet("Flying.Particle") ? ReflectionUtil.lookupEnumSilent(CompParticle.class, this.getString("Flying.Particle").toUpperCase()) : null;
		this.flyingParticleChance = this.getDouble("Flying.Chance", 0.5);

		this.explosionParticleSound = this.getSound("Explosion.Sound", null);
		this.explosionParticle = this.isSet("Explosion.Particle") ? ReflectionUtil.lookupEnumSilent(CompParticle.class, this.getString("Explosion.Particle").toUpperCase()) : null;
		this.explosionParticleChance = this.getDouble("Explosion.Chance", 0.5);
	}

	public String getRawName() {
		return this.getPathPrefix();
	}

	public static Set<String> bombNames() {
		return bombs.getItemNames();
	}

	public static List<MineBomb> bombs() {
		return bombs.getItems();
	}

	public static MineBomb findBomb(String name) {
		return bombs.findItem(name);
	}

	public static void loadBombs() {
		bombs.loadItems();
	}
}
