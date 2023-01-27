package me.whipmegrandma.apollocore.listener;

import me.whipmegrandma.apollocore.enums.CompData;
import me.whipmegrandma.apollocore.model.PlayerCache;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.PlayerUtil;
import org.mineacademy.fo.annotation.AutoRegister;
import org.mineacademy.fo.remain.CompMetadata;
import org.mineacademy.fo.remain.CompParticle;
import org.mineacademy.fo.remain.CompSound;
import org.mineacademy.fo.remain.Remain;

@AutoRegister
public final class TokensListener implements Listener {

	@EventHandler
	public void onInteract(PlayerInteractEvent event) {
		if (!Remain.isInteractEventPrimaryHand(event) || (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK))
			return;

		Player player = event.getPlayer();
		ItemStack hand = player.getItemInHand();
		String key = CompMetadata.getMetadata(hand, CompData.WITHDRAW.toString());
		Integer amount = key != null ? Integer.parseInt(key) : null;

		if (amount != null) {
			PlayerCache cache = PlayerCache.from(player);
			cache.setTokens(cache.getTokens() + amount);

			Common.tell(player, "You deposited " + amount + " tokens.");
			PlayerUtil.takeOnePiece(player, hand);

			CompSound.ANVIL_LAND.play(player);
			CompParticle.EXPLOSION_LARGE.spawn(player, player.getLocation());
		}
	}
}
