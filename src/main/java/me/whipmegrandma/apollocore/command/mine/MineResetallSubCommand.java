package me.whipmegrandma.apollocore.command.mine;

import me.whipmegrandma.apollocore.model.ApolloPlayer;
import me.whipmegrandma.apollocore.model.Mine;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.command.SimpleCommandGroup;
import org.mineacademy.fo.command.SimpleSubCommand;

import java.util.List;

public class MineResetallSubCommand extends SimpleSubCommand {

	protected MineResetallSubCommand(SimpleCommandGroup parent) {
		super(parent, "resetall");

		this.setPermission("apollocore.command.mine.resetall");
	}

	@Override
	protected void onCommand() {
		Common.broadcast("Resetting all mines.");

		for (ApolloPlayer player : ApolloPlayer.getAllCached()) {
			Mine mine = player.getMine();

			if (mine != null)
				mine.resetMine();
		}
	}

	@Override
	protected List<String> tabComplete() {
		return NO_COMPLETE;
	}
}
