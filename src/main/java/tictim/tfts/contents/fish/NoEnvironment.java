package tictim.tfts.contents.fish;

import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.NotNull;

public final class NoEnvironment implements AnglingEnvironment{
	private static final NoEnvironment inst = new NoEnvironment();

	@NotNull
	public static NoEnvironment get(){
		return inst;
	}

	@Override public boolean matches(@NotNull Fluid fluid){
		return false;
	}
	@Override public double getBaseFishingPower(@NotNull FishEnv fishEnv){
		return 0;
	}

	@Override public String toString(){
		return "NoEnvironment";
	}
}
