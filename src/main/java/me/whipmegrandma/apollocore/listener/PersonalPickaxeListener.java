package me.whipmegrandma.apollocore.listener;

import me.whipmegrandma.apollocore.enums.CompMetadataTags;
import me.whipmegrandma.apollocore.menu.PersonalPickaxeEnchantsMenu;
import me.whipmegrandma.apollocore.util.PersonalPickaxeUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.annotation.AutoRegister;
import org.mineacademy.fo.remain.CompMaterial;
import org.mineacademy.fo.remain.CompMetadata;
import org.mineacademy.fo.remain.Remain;

@AutoRegister
public final class PersonalPickaxeListener implements Listener {

	@EventHandler
	public void onDrop(PlayerDropItemEvent event) {
		ItemStack itemStack = event.getItemDrop().getItemStack();

		if (!CompMaterial.isAir(itemStack) && CompMetadata.hasMetadata(itemStack, CompMetadataTags.PICKAXE.toString()))
			event.setCancelled(true);
	}

	@EventHandler
	public void onInventoryMove(InventoryClickEvent event) {
		if (!(event.getWhoClicked() instanceof Player))
			return;

		PlayerInventory inventory = event.getWhoClicked().getInventory();

		ClickType clickType = event.getClick();

		ItemStack cursor = event.getCursor();
		ItemStack clicked = event.getCurrentItem();
		ItemStack number = clickType == ClickType.NUMBER_KEY ? inventory.getItem(event.getHotbarButton()) : null;

		if (!CompMaterial.isAir(cursor) && CompMetadata.hasMetadata(cursor, CompMetadataTags.PICKAXE.toString()))
			event.setCancelled(true);

		if (!CompMaterial.isAir(clicked) && CompMetadata.hasMetadata(clicked, CompMetadataTags.PICKAXE.toString()))
			event.setCancelled(true);

		if (!CompMaterial.isAir(number) && CompMetadata.hasMetadata(number, CompMetadataTags.PICKAXE.toString()))
			event.setCancelled(true);
	}

	@EventHandler
	public void onInventoryDrag(InventoryDragEvent event) {
		if (!(event.getWhoClicked() instanceof Player))
			return;

		ItemStack cursor = event.getOldCursor();

		if (!CompMaterial.isAir(cursor) && CompMetadata.hasMetadata(cursor, CompMetadataTags.PICKAXE.toString()))
			event.setCancelled(true);
	}

	@EventHandler
	public void onDeath(PlayerDeathEvent event) {

		for (ItemStack item : event.getDrops())
			if (!CompMaterial.isAir(item) && CompMetadata.hasMetadata(item, CompMetadataTags.PICKAXE.toString()))
				item.setType(CompMaterial.AIR.toMaterial());
	}

	@EventHandler
	public void onRespawn(PlayerRespawnEvent event) {
		PersonalPickaxeUtil.update(event.getPlayer());
	}

	@EventHandler
	public void onRightClick(PlayerInteractEvent event) {
		if (!Remain.isInteractEventPrimaryHand(event) || (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK))
			return;

		Player player = event.getPlayer();
		ItemStack hand = event.getItem();
		boolean isPersonalPickaxe = hand != null && CompMetadata.hasMetadata(hand, CompMetadataTags.PICKAXE.toString());

		if (isPersonalPickaxe) {
			PersonalPickaxeEnchantsMenu menu = PersonalPickaxeEnchantsMenu.findMenu("First_Menu");

			if (menu != null)
				menu.displayTo(player);
			else
				Common.log("'First_Menu' menu isn't configured in enchants menu.");
		}
	}

	@EventHandler
	public void onDurabilityDamage(PlayerItemDamageEvent event) {
		ItemStack hand = event.getItem();
		boolean isPersonalPickaxe = CompMetadata.hasMetadata(hand, CompMetadataTags.PICKAXE.toString());

		if (isPersonalPickaxe)
			event.setCancelled(true);
	}
}
