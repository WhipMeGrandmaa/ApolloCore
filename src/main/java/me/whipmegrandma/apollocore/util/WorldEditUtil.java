package me.whipmegrandma.apollocore.util;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldedit.util.SideEffectSet;
import lombok.NonNull;
import org.bukkit.Location;
import org.mineacademy.fo.Valid;

import java.io.File;
import java.io.FileInputStream;

public class WorldEditUtil {

	public static void paste(Location to, Clipboard clipboard) {

		try (EditSession session = createEditSession(to.getWorld())) {
			final Operation operation = new ClipboardHolder(clipboard)
					.createPaste(session)
					.to(toWorldEditVector(to))
					.build();

			Operations.complete(operation);

		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	private static EditSession createEditSession(org.bukkit.World bukkitWorld) {
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

	private static BlockVector3 toWorldEditVector(@NonNull Location location) {
		return BlockVector3.at(location.getBlockX(), location.getBlockY(), location.getBlockZ());
	}
}
