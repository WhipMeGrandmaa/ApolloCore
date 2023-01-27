package me.whipmegrandma.apollocore.hook;

import de.slikey.effectlib.EffectManager;
import lombok.Getter;
import me.whipmegrandma.apollocore.ApolloCore;

public final class EffectLibHook {

	@Getter
	private static EffectManager effectManager;

	public static void load() {
		effectManager = new EffectManager(ApolloCore.getInstance());
	}

	public static void disable() {
		effectManager.dispose();
	}

	public static void restart() {
		if (effectManager != null)
			disable();
		
		load();
	}
}
