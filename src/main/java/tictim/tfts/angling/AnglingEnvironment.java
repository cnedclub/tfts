package tictim.tfts.angling;

import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public interface AnglingEnvironment{
	boolean matches(@NotNull Fluid fluid);
	double getBaseFishingPower(@Nullable Set<Biome> biomes);
}
