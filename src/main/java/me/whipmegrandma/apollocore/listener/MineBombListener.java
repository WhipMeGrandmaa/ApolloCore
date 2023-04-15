package me.whipmegrandma.apollocore.listener;

import lombok.Getter;
import me.whipmegrandma.apollocore.enums.CompMetadataTags;
import me.whipmegrandma.apollocore.model.Mine;
import me.whipmegrandma.apollocore.model.MineBomb;
import me.whipmegrandma.apollocore.util.MineBombUtil;
import me.whipmegrandma.apollocore.util.VaultEcoUtil;
import me.whipmegrandma.apollocore.util.WorldGuardUtil;
import org.bukkit.Chunk;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.mineacademy.fo.*;
import org.mineacademy.fo.annotation.AutoRegister;
import org.mineacademy.fo.menu.model.ItemCreator;
import org.mineacademy.fo.model.SimpleSound;
import org.mineacademy.fo.remain.CompMaterial;
import org.mineacademy.fo.remain.CompMetadata;
import org.mineacademy.fo.remain.CompParticle;
import org.mineacademy.fo.remain.Remain;

import java.util.HashMap;
import java.util.Set;
import java.util.UUID;

@AutoRegister
public final class MineBombListener implements Listener {

	@Getter
	private static HashMap<UUID, Long> cooldown = new HashMap<>();

	@EventHandler
	public void onClick(PlayerInteractEvent event) {
		if (!Remain.isInteractEventPrimaryHand(event) || (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK))
			return;

		Player player = event.getPlayer();
		UUID uuid = player.getUniqueId();
		ItemStack hand = event.getItem();
		String data = hand != null ? CompMetadata.getMetadata(hand, CompMetadataTags.MINEBOMB.toString()) : null;

		if (data == null)
			return;

		MineBomb mineBomb = MineBomb.findBomb(data);

		if (mineBomb == null)
			return;

		event.setCancelled(true);

		Long timeNow = System.currentTimeMillis();
		Long timeSaved = cooldown.containsKey(uuid) ? cooldown.get(uuid) : -1;
		Long difference = (timeNow - timeSaved) / 1000;

		if (timeSaved != -1 && difference < this.getCooldown(player)) {
			Common.tell(player, "You must wait " + Common.plural(this.getCooldown(player) - difference, "second") + " before using a mine bomb again.");

			return;
		}

		if (!player.isOp())
			cooldown.put(uuid, timeNow);

		if (player.getGameMode() != GameMode.CREATIVE)
			PlayerUtil.takeOnePiece(player, hand);

		Entity projectile = null;

		if (mineBomb.getFlyingEntity() != null)
			projectile = player.getWorld().spawnEntity(player.getEyeLocation(), mineBomb.getFlyingEntity());

		else if (mineBomb.getFlyingMaterial() != null) {
			projectile = player.getWorld().dropItem(player.getEyeLocation(), ItemCreator.of(mineBomb.getFlyingMaterial()).make());

			CompMetadata.setMetadata(projectile, CompMetadataTags.PROJECTILE.toString());
		}

		projectile.setVelocity(player.getEyeLocation().getDirection().multiply(mineBomb.getThrowVelocity()));

		Entity bomb = projectile;

		if (mineBomb.getThrowSound() != null)
			mineBomb.getThrowSound().play(bomb.getLocation());

		CompParticle flyingParticle = mineBomb.getFlyingParticle();
		SimpleSound flyingSound = mineBomb.getFlyingParticleSound();

		EntityUtil.trackFlying(bomb, () -> {

			if (flyingParticle != null && RandomUtil.chanceD(mineBomb.getFlyingParticleChance()))
				flyingParticle.spawn(bomb.getLocation());

			if (flyingSound != null)
				flyingSound.play(bomb.getLocation());
		});

		EntityUtil.trackFalling(bomb, () -> {
			if (!WorldGuardUtil.testBuild(bomb.getLocation().clone().add(0, -1, 0), player) && !WorldGuardUtil.testBuild(bomb.getLocation(), player)) {
				if (player.getGameMode() != GameMode.CREATIVE)
					MineBombUtil.give(player, mineBomb, 1);

				bomb.remove();

				return;
			}

			Chunk bombChunk = bomb.getLocation().getChunk();

			if (!bombChunk.isLoaded())
				bombChunk.load();

			if (mineBomb.getExplosionParticle() != null)
				mineBomb.getExplosionParticle().spawn(bomb.getLocation());

			if (mineBomb.getExplosionParticleSound() != null)
				mineBomb.getExplosionParticleSound().play(bomb.getLocation());

			Set<Location> sphereBlocks = BlockUtil.getSphere(bomb.getLocation(), mineBomb.getRadius(), false);

			sphereBlocks.removeIf(location -> !WorldGuardUtil.testBuild(location, player) || CompMaterial.isAir(location.getBlock()) || Mine.getWithinMineRegion(location) == null || !Mine.getWithinMineRegion(location).isPlayerAllowed(player));

			if (sphereBlocks.isEmpty()) {
				if (player.getGameMode() != GameMode.CREATIVE)
					MineBombUtil.give(player, mineBomb, 1);

				bomb.remove();

				return;
			}

			VaultEcoUtil.sell(player, sphereBlocks);

			bomb.remove();

			for (Location location : sphereBlocks) {
				if (mineBomb.getExplosionParticle() != null && RandomUtil.chanceD(mineBomb.getExplosionParticleChance()))
					mineBomb.getExplosionParticle().spawn(location);

				Remain.setTypeAndData(location.getBlock(), CompMaterial.AIR);
			}
		});
	}

	@EventHandler
	public void onPickup(PlayerPickupItemEvent event) {
		Item item = event.getItem();

		if (CompMetadata.hasMetadata(item, CompMetadataTags.PROJECTILE.toString()))
			event.setCancelled(true);
	}

	private int getCooldown(Player player) {
		for (int i = 0; i < 100; i++)
			if (PlayerUtil.hasPerm(player, "apollocore.minebomb.cooldown." + i))
				return i;

		return 5;
	}
}
