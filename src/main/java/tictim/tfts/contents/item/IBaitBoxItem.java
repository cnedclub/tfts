package tictim.tfts.contents.item;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public interface IBaitBoxItem{
	void openCurioScreen(@NotNull ServerPlayer player, @NotNull ItemStack stack);
}
