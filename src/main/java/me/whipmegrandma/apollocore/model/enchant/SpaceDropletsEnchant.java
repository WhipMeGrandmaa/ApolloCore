package me.whipmegrandma.apollocore.model.enchant;

import lombok.Getter;
import me.whipmegrandma.apollocore.enums.CompMetadataTags;
import me.whipmegrandma.apollocore.model.EnchantByLevelSettings;
import me.whipmegrandma.apollocore.model.IntermediateEnchant;
import me.whipmegrandma.apollocore.model.Mine;
import me.whipmegrandma.apollocore.settings.EnchantSettings;
import me.whipmegrandma.apollocore.util.VaultEcoUtil;
import me.whipmegrandma.apollocore.util.WorldGuardUtil;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.mineacademy.fo.BlockUtil;
import org.mineacademy.fo.EntityUtil;
import org.mineacademy.fo.RandomUtil;
import org.mineacademy.fo.menu.model.ItemCreator;
import org.mineacademy.fo.model.SimpleEnchantment;
import org.mineacademy.fo.remain.CompMaterial;
import org.mineacademy.fo.remain.CompMetadata;
import org.mineacademy.fo.remain.CompParticle;
import org.mineacademy.fo.remain.Remain;

import java.util.Map;
import java.util.Set;

public final class SpaceDropletsEnchant extends IntermediateEnchant {

	@Getter
	private final static SpaceDropletsEnchant instance = new SpaceDropletsEnchant();

	private SpaceDropletsEnchant() {
		super("SPACE_DROPLETS", "Space Droplets", Integer.MAX_VALUE);
	}

	@Override
	protected void onBreakBlock(int level, BlockBreakEvent event) {
		EnchantByLevelSettings settings = EnchantSettings.spaceDropletsSettings.get(level);

		if (RandomUtil.chanceD(settings.getChance())) {
			Player player = event.getPlayer();
			ItemStack hand = player.getInventory().getItemInMainHand();
			Block block = event.getBlock();
			Location blockLocation = block.getLocation();
			Mine mine = Mine.getWithinMineRegion(block.getLocation());

			if (mine == null || !mine.isPlayerAllowed(player))
				return;

			super.applyEffects(settings, block);

			Entity projectile = null;
			Location dropletLocation = blockLocation.clone().add(0, 10, 0);
			Double dropletVelocity = 2.0;

			if (settings.getSpawnHeightDroplet() != null)
				dropletLocation = block.getLocation().clone().add(0, settings.getSpawnHeightDroplet(), 0);

			if (settings.getVelocityDroplet() != null)
				dropletVelocity = settings.getVelocityDroplet();

			if (settings.getMaterialDroplet() != null) {
				projectile = player.getWorld().dropItem(dropletLocation, ItemCreator.of(settings.getMaterialDroplet()).make());

				//prevented pickup in mine bomb listener
				CompMetadata.setMetadata(projectile, CompMetadataTags.PROJECTILE.toString());
			}

			projectile.setVelocity(dropletLocation.toVector().setX(0).setY(-1).setZ(0).multiply(dropletVelocity));

			Entity droplet = projectile;

			CompParticle dropletParticle = settings.getParticleDroplet();

			EntityUtil.trackFlying(droplet, () -> {

				if (dropletParticle != null && RandomUtil.chanceD(settings.getParticleDropletChance()))
					dropletParticle.spawn(droplet.getLocation());
			});

			EntityUtil.trackFalling(droplet, () -> {

				Chunk bombChunk = droplet.getLocation().getChunk();

				if (!bombChunk.isLoaded())
					bombChunk.load();

				if (settings.getSoundOnImpact() != null)
					settings.getSoundOnImpact().play(droplet.getLocation());

				Set<Location> sphereBlocks = BlockUtil.getSphere(droplet.getLocation(), settings.getCraterRadiusDroplet(), false);

				sphereBlocks.removeIf(location -> block.getLocation().equals(location) || !WorldGuardUtil.testBuild(location, player) || CompMaterial.isAir(location.getBlock()) || Mine.getWithinMineRegion(location) == null || !Mine.getWithinMineRegion(location).isPlayerAllowed(player));
				System.out.println(sphereBlocks);
				double multiplier = 1.0;

				for (Map.Entry<SimpleEnchantment, Integer> enchant : SimpleEnchantment.findEnchantments(hand).entrySet()) {
					SimpleEnchantment enchantment = enchant.getKey();
					Integer levelEnchant = enchant.getValue();

					if (enchantment.equals(StarMultiplerEnchant.getInstance())) {
						EnchantByLevelSettings settingsMultiplier = EnchantSettings.starMultiplierSettings.get(levelEnchant);

						multiplier = settingsMultiplier.getMultiplier();
					}
				}

				VaultEcoUtil.sell(player, sphereBlocks, multiplier);

				droplet.remove();

				for (Location individualLocation : sphereBlocks) {
					Block individualBlock = individualLocation.getBlock();

					if (settings.getParticleBrokenBlocks() != null && RandomUtil.chanceD(settings.getParticleBrokenBlocksChance()) && !CompMaterial.isAir(individualBlock))
						settings.getParticleBrokenBlocks().spawn(individualLocation);

					Remain.setTypeAndData(individualBlock, CompMaterial.AIR);
				}
			});
		}
	}
}
