package me.whipmegrandma.apollocore.util;

import org.bukkit.Location;

public enum DirectionUtil {

	NORTH(0, -1), NORTH_EAST(1, -1),
	EAST(1, 0), SOUTH_EAST(1, 1),
	SOUTH(0, 1), SOUTH_WEST(-1, 1),
	WEST(-1, 0), NORTH_WEST(-1, -1);

	private final double multiplyX;
	private final double multiplyZ;

	DirectionUtil(double multiplyX, double multiplyZ) {
		this.multiplyX = multiplyX;
		this.multiplyZ = multiplyZ;
	}

	public DirectionUtil next() {
		return values()[(ordinal() + 1) % (values().length)];
	}

	public DirectionUtil previous() {
		return values()[(ordinal() - 1 < 0 ? ordinal() - 1 * -1 : ordinal() - 1) % (values().length)];
	}

	public Location transform(Location location, int value) {
		return location.clone().add(value * multiplyX, 0, value * multiplyZ);
	}
}
