package me.whipmegrandma.apollocore.model;

import lombok.Data;
import me.whipmegrandma.apollocore.enums.CompMetadataTags;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.mineacademy.fo.PlayerUtil;
import org.mineacademy.fo.collection.SerializedMap;
import org.mineacademy.fo.menu.model.ItemCreator;
import org.mineacademy.fo.model.ConfigSerializable;
import org.mineacademy.fo.remain.CompMetadata;

@Data
public class ShopItem implements ConfigSerializable {

	private ItemStack item;
	private Integer price;
	private String key;
	private String value;

	public void give(Player player) {
		ItemStack taggedItem = ItemCreator
				.of(this.item)
				.tag(this.key, this.value)
				.make();
		
		PlayerUtil.addItemsOrDrop(player, taggedItem);
	}

	@Override
	public SerializedMap serialize() {
		SerializedMap map = new SerializedMap();

		map.put("Price", this.price);
		map.put("Item", this.item);

		if (this.key != null && this.value != null) {
			map.put("NBTTagKey", this.key);
			map.put("NBTTagValue", this.value);
		}

		return map;
	}

	public static ShopItem of(ItemStack item, Integer price) {
		ShopItem shopItem = new ShopItem();

		shopItem.item = item;
		shopItem.price = price;

		for (CompMetadataTags tag : CompMetadataTags.values())
			if (CompMetadata.hasMetadata(shopItem.item, tag.toString())) {
				shopItem.key = tag.toString();
				shopItem.value = CompMetadata.getMetadata(item, tag.toString());
			}

		return shopItem;
	}

	public static ShopItem deserialize(Object object) {
		return deserialize(SerializedMap.of(object));
	}

	public static ShopItem deserialize(SerializedMap map) {
		ShopItem shopItem = new ShopItem();

		shopItem.price = map.getInteger("Price");
		shopItem.item = map.getItemStack("Item");

		shopItem.key = map.containsKey("NBTTagKey") ? map.getString("NBTTagKey") : null;
		shopItem.value = map.containsKey("NBTTagValue") ? map.getString("NBTTagValue") : null;

		return shopItem;
	}
}
