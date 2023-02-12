package me.whipmegrandma.apollocore.enums;

public enum CompMetadataTags {

	WITHDRAW("ApolloCore:Tokens"),
	PICKAXE("ApolloCore:Pickaxe"),
	MINEBOMB("ApolloCore:MineBomb"),
	PROJECTILE("ApolloCore:Projectile"),
	MINEEDIT("ApolloCore:MineEdit"),
	CENTER("ApolloCore:Center"),
	HOME("ApolloCore:Home");

	private final String compData;

	private CompMetadataTags(String compData) {
		this.compData = compData;
	}

	@Override
	public String toString() {
		return compData;
	}
}
