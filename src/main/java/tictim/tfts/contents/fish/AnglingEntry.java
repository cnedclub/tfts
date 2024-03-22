package tictim.tfts.contents.fish;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

// TODO
public interface AnglingEntry<T extends AnglingEntry<T>>{
	@NotNull AnglingEntryType<T> type();

	double getWeight(@NotNull Player player, @NotNull BlockPos pos, @NotNull AnglingEnvironment environment);

	void getLoot(@NotNull List<ItemStack> loots);
	int getExperience(@NotNull RandomSource randomSource);

	@NotNull NibbleBehavior nibbleBehavior();
	double baitConsumptionChance();
}
