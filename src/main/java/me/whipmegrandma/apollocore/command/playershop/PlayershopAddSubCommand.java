package me.whipmegrandma.apollocore.command.playershop;

import me.whipmegrandma.apollocore.enums.CompMetadataTags;
import me.whipmegrandma.apollocore.model.ApolloPlayer;
import me.whipmegrandma.apollocore.model.ShopItem;
import me.whipmegrandma.apollocore.settings.PlayerShopSettings;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.mineacademy.fo.ItemUtil;
import org.mineacademy.fo.PlayerUtil;
import org.mineacademy.fo.command.SimpleCommandGroup;
import org.mineacademy.fo.command.SimpleSubCommand;
import org.mineacademy.fo.remain.CompMaterial;
import org.mineacademy.fo.remain.CompMetadata;

import java.text.NumberFormat;
import java.util.List;

public class PlayershopAddSubCommand extends SimpleSubCommand {

	protected PlayershopAddSubCommand(SimpleCommandGroup parent) {
		super(parent, "add");

		this.setPermission("apollocore.command.playershop.add");
		this.setUsage("<price>");
		this.setMinArguments(1);
	}

	@Override
	protected void onCommand() {
		checkConsole();

		ApolloPlayer apolloPlayer = ApolloPlayer.from(getPlayer());
		PlayerInventory inventory = getPlayer().getInventory();
		ItemStack hand = inventory.getItemInMainHand();
		Integer price = this.findNumber(0, "The price must be a number.");

		checkBoolean(!CompMaterial.isAir(hand), "You must be holding an item you wish to sell.");
		checkBoolean(price > 0, "The price must be above 0.");
		
		checkBoolean(apolloPlayer.getNumberOfShopItems() < getAmountOfShopItems(), "You cannot add any more items to your player shop.");

		for (CompMetadataTags tag : PlayerShopSettings.bannedSpecialItems)
			checkBoolean(!CompMetadata.hasMetadata(hand, tag.toString()), "You cannot add this special item to your player shop.");

		for (CompMaterial material : PlayerShopSettings.bannedItems)
			checkBoolean(hand.getType() != material.toMaterial(), "You cannot add this item to your player shop.");

		checkBoolean(price >= PlayerShopSettings.minimumPrice, "The minimum price to add an item to your player shop is $" + NumberFormat.getInstance().format(PlayerShopSettings.minimumPrice) + ".");
		checkBoolean(price <= PlayerShopSettings.maximumPrice, "The maximum price to add an item to your player shop is $" + NumberFormat.getInstance().format(PlayerShopSettings.maximumPrice) + ".");

		ShopItem item = ShopItem.of(hand, price);
		apolloPlayer.addShopItem(item);

		ItemMeta itemMeta = hand.getItemMeta();

		tell("You have listed " + hand.getAmount() + " " + (!itemMeta.getDisplayName().isEmpty() ? itemMeta.getDisplayName() : ItemUtil.bountifyCapitalized(hand.getType()).toLowerCase()) + " &ffor $" + NumberFormat.getInstance().format(price) + " in your player shop!");
		inventory.setItem(inventory.getHeldItemSlot(), CompMaterial.AIR.toItem());
	}

	@Override
	protected List<String> tabComplete() {
		return NO_COMPLETE;
	}

	private int getAmountOfShopItems() {
		if (getPlayer().isOp())
			return Integer.MAX_VALUE;

		for (int i = 0; i < 200; i++)
			if (PlayerUtil.hasPerm(getPlayer(), "apollocore.playershop.amount." + i))
				return i;

		return 45;
	}
}
