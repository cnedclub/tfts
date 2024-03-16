package tictim.tfts.angling;

import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public final class NoEnvironment implements AnglingEnvironment{
	private static final NoEnvironment inst = new NoEnvironment();

	@NotNull
	public static NoEnvironment get(){
		return inst;
	}

	@Override public boolean matches(@NotNull Fluid fluid){
		return false;
	}
	@Override public double getBaseFishingPower(@Nullable Set<Biome> biomes){
		return 0;
	}

	@Override public String toString(){
		return "NoEnvironment";
	}
}
