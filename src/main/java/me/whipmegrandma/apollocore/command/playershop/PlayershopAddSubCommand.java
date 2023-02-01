package me.whipmegrandma.apollocore.command.playershop;

import me.whipmegrandma.apollocore.model.PlayerCache;
import me.whipmegrandma.apollocore.model.ShopItem;
import org.bukkit.inventory.ItemStack;
import org.mineacademy.fo.ItemUtil;
import org.mineacademy.fo.command.SimpleCommandGroup;
import org.mineacademy.fo.command.SimpleSubCommand;
import org.mineacademy.fo.remain.CompMaterial;

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

		ItemStack hand = getPlayer().getInventory().getItemInMainHand();
		Integer price = this.findNumber(0, "The price must be a number.");

		checkBoolean(!CompMaterial.isAir(hand), "You must be holding an item you wish to sell.");
		checkBoolean(price > 0, "The price must be above 0.");
		
		ShopItem item = ShopItem.of(hand, price);
		PlayerCache.from(getPlayer()).addShopItem(item);

		tell("You have listed " + hand.getAmount() + " " + ItemUtil.bountifyCapitalized(hand.getType()).toLowerCase() + " for $" + NumberFormat.getInstance().format(price) + " in your player shop!");
		getPlayer().getInventory().removeItem(hand);
	}

	@Override
	protected List<String> tabComplete() {
		return NO_COMPLETE;
	}
}
