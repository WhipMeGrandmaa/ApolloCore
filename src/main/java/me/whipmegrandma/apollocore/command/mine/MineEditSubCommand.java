package me.whipmegrandma.apollocore.command.mine;

import lombok.Getter;
import lombok.Setter;
import me.whipmegrandma.apollocore.enums.Operator;
import me.whipmegrandma.apollocore.manager.MineWorldManager;
import me.whipmegrandma.apollocore.model.Mine;
import me.whipmegrandma.apollocore.model.tool.MineRegionTool;
import me.whipmegrandma.apollocore.settings.MineSettings;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.command.SimpleCommandGroup;
import org.mineacademy.fo.command.SimpleSubCommand;
import org.mineacademy.fo.visual.VisualizedRegion;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class MineEditSubCommand extends SimpleSubCommand {

	@Getter
	private static MineEditSubCommand instance;

	private UUID editor;
	private ItemStack[] preEditInventory;
	private Location preEditLocation;
	private Mine mine;
	private Location nextFreeLocation;
	private Location centerLocation;
	private Location homeLocation;
	private VisualizedRegion mineRegion = new VisualizedRegion();

	protected MineEditSubCommand(SimpleCommandGroup parent) {
		super(parent, "edit");

		this.setPermission("apollocore.command.mine.edit");

		instance = this;
	}

	@Override
	protected void onCommand() {
		checkConsole();

		Player player = getPlayer();
		MineSettings mineSettings = MineSettings.getInstance();

		checkBoolean(MineSettings.getInstance().getSchematic().exists(), "The schematic is null. You must put a schematic named 'mine' in the schematics folder.");

		if (player.getUniqueId().equals(this.editor)) {
			tell("Saved settings and exiting mine editor.");

			player.teleport(preEditLocation);
			player.getInventory().setContents(preEditInventory);

			if (mineRegion.isWhole())
				mineSettings.setDefaultMineRegion(MineWorldManager.transform(this.mineRegion, this.nextFreeLocation, Operator.MINUS));

			if (centerLocation != null)
				mineSettings.setDefaultCenter(MineWorldManager.transform(this.centerLocation, this.nextFreeLocation, Operator.MINUS));

			if (homeLocation != null)
				mineSettings.setDefaultHomeLocation(MineWorldManager.transform(this.homeLocation, this.nextFreeLocation, Operator.MINUS));

			this.mine.delete();

			this.editor = null;
			this.mine = null;
			this.nextFreeLocation = null;
			this.mineRegion = new VisualizedRegion();
			this.centerLocation = null;
			this.homeLocation = null;

			Mine.updateAll();

			return;
		}

		checkBoolean(editor == null, "You have to wait for the person using the editor to finish first.");

		this.editor = player.getUniqueId();
		this.preEditInventory = player.getInventory().getContents();
		this.preEditLocation = player.getLocation();
		this.nextFreeLocation = MineWorldManager.getNextFreeLocation();

		this.mine = Mine.create(nextFreeLocation, false);

		this.mine.teleportToHome(getPlayer(), nextFreeLocation, false);
		tell("Entering mine editor. Use '/mine edit' again to exit and save.");

		Common.runLater(30, () -> {
			player.setGameMode(GameMode.CREATIVE);
			player.setFlying(true);
		});

		PlayerInventory inventory = player.getInventory();

		inventory.setContents(new ItemStack[]{});

		inventory.setItem(4, MineRegionTool.getInstance().getItem());
	}

	@Override
	protected List<String> tabComplete() {
		return NO_COMPLETE;
	}
}
