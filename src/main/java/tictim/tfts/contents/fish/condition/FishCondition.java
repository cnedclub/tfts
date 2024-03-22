package tictim.tfts.contents.fish.condition;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;
import tictim.tfts.contents.fish.AnglingEnvironment;

public interface FishCondition<T extends FishCondition<T>>{
	FishConditionType<T> type();
	boolean matches(@NotNull Player player, @NotNull BlockPos pos, @NotNull AnglingEnvironment environment);
}
