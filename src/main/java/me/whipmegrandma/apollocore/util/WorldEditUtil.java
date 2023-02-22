package me.whipmegrandma.apollocore.util;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.function.pattern.Pattern;
import com.sk89q.worldedit.function.pattern.RandomPattern;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldedit.util.SideEffectSet;
import lombok.NonNull;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.Valid;
import org.mineacademy.fo.region.Region;
import org.mineacademy.fo.remain.CompMaterial;

import java.io.File;
import java.io.FileInputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WorldEditUtil {

	public static void paste(Location to, Clipboard clipboard) {
		Common.runAsync(() -> {
			try (EditSession session = createEditSession(to.getWorld())) {
				final Operation operation = new ClipboardHolder(clipboard)
						.createPaste(session)
						.to(toWorldEditVector(to))
						.build();

				Operations.complete(operation);

			} catch (Throwable t) {
				t.printStackTrace();
			}
		});
	}

	public static void setBlocks(Block block, CompMaterial material) {
		setBlocks(block.getLocation(), Collections.singletonList(material));
	}

	public static void setBlocks(Location location, CompMaterial material) {
		setBlocks(location, Collections.singletonList(material));
	}

	public static void setBlocks(Region region, CompMaterial material) {
		setBlocks(region, Collections.singletonList(material));
	}

	public static void setBlocks(Location location, List<CompMaterial> materials) {
		setBlocks(new Region(location, location), materials);
	}

	public static void setBlocks(Region region, List<CompMaterial> materials) {
		Map<CompMaterial, Double> materialWithChances = new HashMap<>();

		for (CompMaterial material : materials) {
			Double chance = (double) (1 / materials.size());

			materialWithChances.put(material, chance);
		}

		setBlocks(region, materialWithChances);
	}

	public static void setBlocks(Region region, Map<CompMaterial, Double> materials) {
		Common.runAsync(() -> {
			try (EditSession session = createEditSession(region.getWorld())) {

				BlockVector3 primary = toWorldEditVector(region.getPrimary());
				BlockVector3 secondary = toWorldEditVector(region.getSecondary());
				CuboidRegion selection = new CuboidRegion(new BukkitWorld(region.getWorld()), primary, secondary);

				RandomPattern randomPattern = new RandomPattern();

				for (Map.Entry<CompMaterial, Double> entry : materials.entrySet()) {
					Pattern pattern = getPattern(entry.getKey().toMaterial());
					Double chance = entry.getValue();

					randomPattern.add(pattern, chance);
				}

				session.setBlocks(selection, randomPattern);
			} catch (Throwable t) {
				t.printStackTrace();
			}
		});
	}

	public static Pattern getPattern(Material material) {
		return BukkitAdapter.adapt(material.createBlockData());
	}

	public static EditSession createEditSession(org.bukkit.World bukkitWorld) {
		final BukkitWorld world = new BukkitWorld(bukkitWorld);
		final EditSession session = WorldEdit.getInstance().newEditSession(world);

		session.setSideEffectApplier(SideEffectSet.defaults());

		return session;
	}

	public static Clipboard loadSchematic(File file) {

		try {
			final ClipboardFormat format = ClipboardFormats.findByFile(file);
			Valid.checkNotNull(format, "Null schematic file format " + file + " (file corrupted or WorldEdit outdated - or too new?)!");

			final ClipboardReader reader = format.getReader(new FileInputStream(file));
			final Clipboard schematic = reader.read();
			Valid.checkNotNull(schematic, "Failed to read schematic from " + file);

			return schematic;

		} catch (final Throwable t) {
			throw new RuntimeException(t);
		}
	}

	public static BlockVector3 toWorldEditVector(@NonNull Location location) {
		return BlockVector3.at(location.getBlockX(), location.getBlockY(), location.getBlockZ());
	}
}
