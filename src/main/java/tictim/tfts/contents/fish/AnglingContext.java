package tictim.tfts.contents.fish;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tictim.tfts.contents.entity.TFTSHook;

public final class AnglingContext{
	@NotNull public final TFTSHook hook;
	@NotNull public final ServerLevel level;
	@NotNull public final Player player;
	@NotNull public final BlockPos pos;
	@NotNull public final AnglingEnvironment environment;

	private boolean baitCached;
	@Nullable private BaitStat bait;

	public AnglingContext(
			@NotNull TFTSHook hook,
			@NotNull ServerLevel level,
			@NotNull Player player,
			@NotNull BlockPos pos,
			@NotNull AnglingEnvironment environment
	){
		this.hook = hook;
		this.level = level;
		this.player = player;
		this.pos = pos;
		this.environment = environment;
	}

	@Nullable public BaitStat bait(){
		if(!this.baitCached){
			this.baitCached = true;
			this.bait = AnglingUtils.getBaitStat(this.player, this.level.getServer().registryAccess());
		}
		return this.bait;
	}

	public boolean hasBait(){
		return bait()!=null;
	}

	public double additionalFishingPower(){
		return 0; // TODO do some shit
	}
}
