package me.whipmegrandma.apollocore.command.tokens;

import org.mineacademy.fo.Common;
import org.mineacademy.fo.annotation.AutoRegister;
import org.mineacademy.fo.command.SimpleCommandGroup;
import org.mineacademy.fo.model.SimpleComponent;

import java.util.Arrays;
import java.util.List;

@AutoRegister
public final class TokensCommandGroup extends SimpleCommandGroup {

	public TokensCommandGroup() {
		super("tokens");
	}

	@Override
	protected void registerSubcommands() {
		this.registerSubcommand(new TokensSetSubCommand(this));
		this.registerSubcommand(new TokensAddSubCommand(this));
		this.registerSubcommand(new TokensResetSubCommand(this));
		this.registerSubcommand(new TokensBalanceSubCommand(this));
		this.registerSubcommand(new TokensWithdrawSubCommand(this));
	}

	@Override
	protected String[] getHelpHeader() {
		return new String[]{Common.colorize("", Common.colorize("&d&lTOKENS HELP MENU"))};
	}


	@Override
	protected List<SimpleComponent> getNoParamsHeader() {
		return Arrays.asList(SimpleComponent.of("{prefix} Use /tokens ? to list the commands."));
	}
}
