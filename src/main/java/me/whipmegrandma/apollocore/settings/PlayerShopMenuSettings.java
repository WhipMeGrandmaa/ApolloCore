package me.whipmegrandma.apollocore.settings;

import org.mineacademy.fo.menu.button.Button;
import org.mineacademy.fo.remain.CompMaterial;
import org.mineacademy.fo.settings.YamlStaticConfig;

import java.util.Arrays;
import java.util.List;


public class PlayerShopMenuSettings extends YamlStaticConfig {

	public static String marketPlaceMenuTitle;
	public static String marketPlaceIndividualTitle;
	public static List<String> marketPlaceIndividualLore;

	public static String pShopMenuTitle;
	public static List<String> pShopIndividualOtherLore;
	public static List<String> pShopIndividualYoursLore;

	public static String confirmMenuTitle;
	public static List<String> confirmMenuLore;

	public static CompMaterial confirmMaterial;
	public static String confirmTitle;

	public static CompMaterial denyMaterial;
	public static String denyTitle;

	public static CompMaterial infoMaterial;
	public static String infoTitle;
	public static List<String> infoLore;

	public static CompMaterial refreshMaterial;
	public static String refreshTitle;
	public static List<String> refreshLore;

	public static CompMaterial sortedNewMaterial;
	public static String sortedNewTitle;
	public static List<String> sortedNewLore;

	public static CompMaterial sortedMostMaterial;
	public static String sortedMostTitle;
	public static List<String> sortedMostLore;

	public static CompMaterial nextPageMaterial;
	public static String nextPageTitle;

	public static CompMaterial previousPageMaterial;
	public static String previousPageTitle;

	public static CompMaterial personalShopMaterial;
	public static String personalShopTitle;
	public static List<String> personalShopLore;

	public static CompMaterial marketMaterial;
	public static String marketTitle;
	public static List<String> marketLore;

	@Override
	protected void onLoad() throws Exception {
		this.loadConfiguration("menus/playershop.yml");
	}

	private static void init() {
		setPathPrefix("Market_Place");

		marketPlaceMenuTitle = isSet("Title") ? getString("Title") : "Market Place";
		marketPlaceIndividualTitle = isSet("Individual_Title") ? getString("Individual_Title") : "&ePlayer Shop of &d%player_name%";
		marketPlaceIndividualLore = isSet("Individual_Lore") ? getStringList("Individual_Lore") : null;

		setPathPrefix("Player_Shop");

		pShopMenuTitle = isSet("Title") ? getString("Title") : "&ePlayer Shop of &d%player_name%";
		pShopIndividualYoursLore = isSet("Individual_Lore_Added_On.Yours") ? getStringList("Individual_Lore_Added_On.Yours") : null;
		pShopIndividualOtherLore = isSet("Individual_Lore_Added_On.Other") ? getStringList("Individual_Lore_Added_On.Other") : null;

		setPathPrefix("Confirm_Menu");

		confirmMenuTitle = isSet("Title") ? getString("Title") : "&ePlayer Shop of &d%player_name%";
		confirmMenuLore = isSet("Individual_Lore_Added_On") ? getStringList("Individual_Lore_Added_On") : null;

		setPathPrefix("Buttons.Info");

		infoMaterial = isSet("Material") ? getMaterial("Material") : CompMaterial.END_CRYSTAL;
		infoTitle = isSet("Title") ? getString("Title") : "&d&lInformation";
		infoLore = isSet("Lore") ? getStringList("Lore") : Arrays.asList("", "&7Command: &f/playershop add <price>", "", "&fShift Right Click &7to remove", "&7an item from your player shop.");

		Button.setInfoButtonMaterial(infoMaterial);
		Button.setInfoButtonTitle(infoTitle);

		setPathPrefix("Buttons.Refresh");

		refreshMaterial = isSet("Material") ? getMaterial("Material") : CompMaterial.CHEST;
		refreshTitle = isSet("Title") ? getString("Title") : "&d&lRefresh";
		refreshLore = isSet("Lore") ? getStringList("Lore") : Arrays.asList("", "&7Refresh the listings.");

		setPathPrefix("Buttons.Sort.New");

		sortedNewMaterial = isSet("Material") ? getMaterial("Material") : CompMaterial.CHARCOAL;
		sortedNewTitle = isSet("Title") ? getString("Title") : "&d&lSorting";
		sortedNewLore = isSet("Lore") ? getStringList("Lore") : Arrays.asList("", "&7Sorted by: &dNewest Item");

		setPathPrefix("Buttons.Sort.Most_Items");

		sortedMostMaterial = isSet("Material") ? getMaterial("Material") : CompMaterial.CHARCOAL;
		sortedMostTitle = isSet("Title") ? getString("Title") : "&d&lSorting";
		sortedMostLore = isSet("Lore") ? getStringList("Lore") : Arrays.asList("", "&7Sorted by: &dMost Items");

		setPathPrefix("Buttons.Next_Page");

		nextPageMaterial = isSet("Material") ? getMaterial("Material") : CompMaterial.GREEN_SHULKER_BOX;
		nextPageTitle = isSet("Title") ? getString("Title") : "&aNext Page";

		setPathPrefix("Buttons.Previous_Page");

		previousPageMaterial = isSet("Material") ? getMaterial("Material") : CompMaterial.RED_SHULKER_BOX;
		previousPageTitle = isSet("Title") ? getString("Title") : "&cPrevious Page";

		setPathPrefix("Buttons.Personal_Shop");

		personalShopMaterial = isSet("Material") ? getMaterial("Material") : CompMaterial.CHEST;
		personalShopTitle = isSet("Title") ? getString("Title") : "&d&lPersonal Shop";
		personalShopLore = isSet("Lore") ? getStringList("Lore") : Arrays.asList("", "&7Click to go to your shop.");

		setPathPrefix("Buttons.Market");

		marketMaterial = isSet("Material") ? getMaterial("Material") : CompMaterial.ENDER_CHEST;
		marketTitle = isSet("Title") ? getString("Title") : "&d&lMarket";
		marketLore = isSet("Lore") ? getStringList("Lore") : Arrays.asList("", "&7Click to go to the market.");

		setPathPrefix("Buttons.Confirm");

		confirmMaterial = isSet("Material") ? getMaterial("Material") : CompMaterial.ENDER_CHEST;
		confirmTitle = isSet("Title") ? getString("Title") : "&aCONFIRM";

		setPathPrefix("Buttons.Deny");

		denyMaterial = isSet("Material") ? getMaterial("Material") : CompMaterial.ENDER_CHEST;
		denyTitle = isSet("Title") ? getString("Title") : "&cDENY";
	}
}
