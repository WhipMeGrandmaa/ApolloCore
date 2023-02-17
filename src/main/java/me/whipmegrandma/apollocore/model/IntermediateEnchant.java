package me.whipmegrandma.apollocore.model;

import lombok.Getter;
import me.whipmegrandma.apollocore.manager.EnchantsManager;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.potion.PotionEffectType;
import org.mineacademy.fo.RandomUtil;
import org.mineacademy.fo.model.SimpleEnchantment;
import org.mineacademy.fo.remain.CompMaterial;
import org.mineacademy.fo.remain.Remain;

import java.util.ArrayList;
import java.util.List;

@Getter
public abstract class IntermediateEnchant extends SimpleEnchantment {

	private PotionEffectType effectType;

	protected IntermediateEnchant(String name, String displayName, int maxLevel) {
		super(displayName, maxLevel);

		EnchantsManager.register(name, this);
	}

	protected IntermediateEnchant(String name, String displayName, int maxLevel, PotionEffectType effectType) {
		this(name, displayName, maxLevel);

		this.effectType = effectType;
	}

	protected void applyEffects(EnchantByLevelSettings settings, Block brokenBlock) {
		this.applyEffects(settings, brokenBlock, new ArrayList<>());
	}

	protected void applyEffects(EnchantByLevelSettings settings, Block brokenBlock, List<Block> blocks) {
		Location blockLocation = brokenBlock.getLocation();

		if (settings.getSoundOnBreak() != null)
			settings.getSoundOnBreak().play(blockLocation);

		if (settings.getParticleOnBreak() != null)
			settings.getParticleOnBreak().spawn(blockLocation);

		if (settings.getLightningOnBreak() != null)
			blockLocation.getWorld().strikeLightningEffect(blockLocation);

		for (Block individualBlock : blocks) {
			if (settings.getParticleBrokenBlocks() != null && RandomUtil.chanceD(settings.getParticleBrokenBlocksChance()) && !CompMaterial.isAir(individualBlock))
				settings.getParticleBrokenBlocks().spawn(individualBlock.getLocation());

			Remain.setTypeAndData(individualBlock, CompMaterial.AIR);
		}
	}
}
