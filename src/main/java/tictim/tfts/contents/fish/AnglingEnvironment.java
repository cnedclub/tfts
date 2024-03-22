package tictim.tfts.contents.fish;

import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public interface AnglingEnvironment{
	boolean matches(@NotNull Fluid fluid);
	double getBaseFishingPower(@NotNull FishEnv fishEnv);

	default void processLoot(@NotNull ItemEntity itemEntity){}
	default void processExp(@NotNull ExperienceOrb experienceOrb){}
}
