package tictim.tfts.contents.fish;

import org.jetbrains.annotations.NotNull;

public final class NoEnvironment implements AnglingEnvironment{
	private static final NoEnvironment inst = new NoEnvironment();

	@NotNull public static NoEnvironment get(){
		return inst;
	}

	@Override public double getBaseFishingPower(@NotNull FishEnv fishEnv){
		return 0;
	}

	@Override public String toString(){
		return "NoEnvironment";
	}
}
