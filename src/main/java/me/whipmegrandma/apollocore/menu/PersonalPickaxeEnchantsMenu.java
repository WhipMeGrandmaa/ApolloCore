package me.whipmegrandma.apollocore.menu;

import lombok.Data;
import me.whipmegrandma.apollocore.manager.EnchantsManager;
import me.whipmegrandma.apollocore.model.ApolloPlayer;
import me.whipmegrandma.apollocore.util.EnchantPurchaseUtil;
import me.whipmegrandma.apollocore.util.PlaceholderUtil;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.ItemUtil;
import org.mineacademy.fo.MathUtil;
import org.mineacademy.fo.Valid;
import org.mineacademy.fo.collection.SerializedMap;
import org.mineacademy.fo.menu.Menu;
import org.mineacademy.fo.menu.button.Button;
import org.mineacademy.fo.menu.model.ItemCreator;
import org.mineacademy.fo.remain.CompMaterial;
import org.mineacademy.fo.settings.ConfigItems;
import org.mineacademy.fo.settings.YamlConfig;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PersonalPickaxeEnchantsMenu extends YamlConfig {

	private final static ConfigItems<PersonalPickaxeEnchantsMenu> menus = ConfigItems.fromFile("", "menus/pickaxeenchants.yml", PersonalPickaxeEnchantsMenu.class);

	private final String name;

	private String title;
	private int size;
	private List<ButtonData> buttons;

	private PersonalPickaxeEnchantsMenu(String name) {
		this.name = name;

		this.setPathPrefix(name);
		this.loadConfiguration("menus/pickaxeenchants.yml");
	}

	@Override
	protected void onLoad() {
		this.title = isSet("Title") ? getString("Title") : "Menu";
		this.size = isSet("Size") ? (int) MathUtil.calculate(getString("Size")) : 36;
		this.buttons = this.loadButtons();
	}

	private List<ButtonData> loadButtons() {
		List<ButtonData> data = new ArrayList<>();

		for (Map.Entry<String, Object> entry : getMap("Buttons").entrySet()) {
			SerializedMap settings = SerializedMap.of(entry.getValue());

			data.add(ButtonData.deserialize(settings));
		}

		return data;
	}

	public void displayTo(Player player) {
		this.toMenu(player).displayTo(player);
	}

	private Menu toMenu(Player player) {
		return this.toMenu(null, player);
	}

	private Menu toMenu(Menu parent, Player player) {
		Map<Integer, Button> buttons = this.getButtons(player);

		return new Menu(parent) {

			{
				this.setTitle(title);
				this.setSize(size);
			}

			@Override
			protected List<Button> getButtonsToAutoRegister() {
				return new ArrayList<>(buttons.values());
			}

			@Override
			public ItemStack getItemAt(int slot) {

				if (buttons.containsKey(slot))
					return buttons.get(slot).getItem();

				return NO_ITEM;
			}
		};
	}

	private Map<Integer, Button> getButtons(Player player) {
		Map<Integer, Button> buttons = new HashMap<>();

		for (ButtonData data : this.buttons) {
			buttons.put(data.getSlot(), new Button() {

				@Override
				public void onClickedInMenu(Player player, Menu menu, ClickType click) {
					ApolloPlayer cache = ApolloPlayer.from(player);

					if (data.enchantment != null) {
						EnchantPurchaseUtil.PurchaseEnum result = EnchantPurchaseUtil.buy(player, data.enchantment, data.getTokens0(player), data.getMaxLevel());

						switch (result) {

							case SUCCESS:
								menu.restartMenu(data.getPurchaseMessage()
										.replace("%enchant%", ItemUtil.bountifyCapitalized(data.enchantment.getKey().getKey()))
										.replace("%level%", cache.getEnchantLevel(data.enchantment).toString()));

								break;
							case INSUFFICIENT_BALANCE:
								menu.restartMenu(data.getInsufficientMessage());

								break;
							case MAX_LEVEL:
								menu.restartMenu(data.getMaxLevelMessage());

								break;
						}
					}

					if (data.getMenuToOpen() != null) {
						PersonalPickaxeEnchantsMenu otherMenu = findMenu(data.getMenuToOpen());

						if (otherMenu == null)
							menu.animateTitle("Invalid menu: " + data.getMenuToOpen());
						else
							otherMenu.toMenu(menu, player).displayTo(player);
					}

					if (data.getCommand() != null)
						for (String command : data.getCommand())
							Common.dispatchCommandAsPlayer(player, command);

				}

				@Override
				public ItemStack getItem() {
					ApolloPlayer cache = ApolloPlayer.from(player);
					Integer level = cache.getEnchantLevel(data.enchantment);

					String title = PlaceholderUtil.set(player, data.getTitle());
					List<String> lore = PlaceholderUtil.set(player, data.getLore());

					String formattedTitle = null;
					List<String> formattedLore = null;

					if (data.enchantment != null && level != null) {
						formattedTitle = title.replace("%enchant%", ItemUtil.bountifyCapitalized(data.enchantment.getKey().getKey()))
								.replace("%level%", level.toString())
								.replace("%price%", NumberFormat.getInstance().format(data.getTokens0(player)));
						formattedLore = new ArrayList<>();

						for (String line : lore)
							formattedLore.add(line.replace("%enchant%", ItemUtil.bountifyCapitalized(data.enchantment.getKey().getKey()))
									.replace("%level%", level.toString())
									.replace("%price%", NumberFormat.getInstance().format(data.getTokens0(player))));
					}

					ItemCreator maker = ItemCreator.of(data.getMaterial(), formattedTitle != null ? formattedTitle : title, formattedLore != null ? formattedLore : lore)
							.glow(data.isGlow());

					if (data.playerSkull) {
						String playerName = data.getPlayerSkullName();

						maker.skullOwner(playerName != null ? playerName : player.getName());
					}

					return maker.make();
				}
			});
		}

		return buttons;
	}

	@Data
	private static class ButtonData {

		private String title;
		private List<String> lore;
		private int slot;
		private boolean glow;
		private CompMaterial material;
		private boolean playerSkull;
		private String playerSkullName;

		private Enchantment enchantment;
		private Integer maxLevel;
		private String levelZero;
		private List<String> command;
		private String menuToOpen;
		private Integer tokens;
		private Integer increaseTokens;
		private SerializedMap tokensByLevel;
		private String purchaseMessage;
		private String insufficientMessage;
		private String maxLevelMessage;

		public static ButtonData deserialize(SerializedMap map) {
			ButtonData button = new ButtonData();

			button.title = map.getString("Title", "Button");

			button.lore = map.getStringList("Lore", null);

			button.slot = map.containsKey("Slot") ? map.getInteger("Slot") : -1;
			Valid.checkBoolean(button.slot != -1, "Slot isn't set for " + map);

			button.glow = map.getBoolean("Glow", false);

			button.material = map.getMaterial("Material", CompMaterial.OAK_BUTTON);

			button.playerSkull = map.getBoolean("Player_Skull", false);

			button.playerSkullName = map.getString("Player_Skull_Name", null);

			SerializedMap click = map.getMap("Click");

			button.enchantment = click.containsKey("Enchantment") ? EnchantsManager.getByName(click.getString("Enchantment")) : null;

			button.maxLevel = click.containsKey("Enchantment") && click.containsKey("Max_Level") ? click.getInteger("Max_Level") : 0;

			button.levelZero = click.getString("Level_Zero", "&aClick to purchase!");

			button.command = click.getStringList("Command", null);

			button.menuToOpen = click.getString("Menu_To_Open", null);

			button.tokens = click.getInteger("Tokens", 0);

			button.increaseTokens = click.getInteger("Increase_Tokens", 0);

			button.tokensByLevel = click.getMap("Tokens_By_Level");

			button.purchaseMessage = click.getString("Purchase_Message", "&aPurchased %enchant% %level%");

			button.insufficientMessage = click.getString("Insufficient_Message", "&cInsufficient tokens");

			button.maxLevelMessage = click.getString("Max_Level_Message", "&aMax level");

			return button;
		}

		public Integer getTokens0(Player player) {
			if (this.tokens == null && this.tokensByLevel.isEmpty())
				return 0;

			ApolloPlayer cache = ApolloPlayer.from(player);

			int playerLevel = cache.getEnchantLevel(this.enchantment);

			for (Map.Entry<String, Object> entry : this.tokensByLevel.entrySet()) {
				int level = Valid.isInteger(entry.getKey()) ? Integer.parseInt(entry.getKey()) : 0;
				int tokens = Valid.isInteger(entry.getValue().toString()) ? Integer.parseInt(entry.getValue().toString()) : 0;

				if (playerLevel + 1 == level)
					return tokens;

			}

			return this.tokens + (playerLevel * this.increaseTokens);
		}
	}

	public static PersonalPickaxeEnchantsMenu findMenu(String name) {
		return menus.findItem(name);
	}

	public static void loadMenus() {
		menus.loadItems();
	}
}
