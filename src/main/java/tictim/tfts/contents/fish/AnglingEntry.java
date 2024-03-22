package tictim.tfts.contents.fish;

import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface AnglingEntry<T extends AnglingEntry<T>>{
	@NotNull AnglingEntryType<T> type();

	double getWeight(@NotNull AnglingContext context);
	@NotNull NibbleBehavior getNibbleBehavior(@NotNull AnglingContext context);
	double getBaitConsumptionChance(@NotNull AnglingContext context);

	void getLoot(@NotNull AnglingContext context, @NotNull List<ItemStack> loots);
	int getExperience(@NotNull AnglingContext context, @NotNull RandomSource randomSource);
}
