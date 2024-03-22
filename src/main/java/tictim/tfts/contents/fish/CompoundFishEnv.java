package tictim.tfts.contents.fish;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

public final class CompoundFishEnv implements FishEnv{
	private final EnumSet<PrimitiveFishEnv> delegate = EnumSet.noneOf(PrimitiveFishEnv.class);

	@ApiStatus.Internal CompoundFishEnv(@NotNull FishEnv @NotNull ... envs){
		for(FishEnv env : envs) this.delegate.addAll(env.asSet());
	}
	@ApiStatus.Internal CompoundFishEnv(@NotNull List<? extends FishEnv> envs){
		for(FishEnv env : envs) this.delegate.addAll(env.asSet());
	}

	@Override @NotNull @Unmodifiable public Set<@NotNull PrimitiveFishEnv> asSet(){
		return Collections.unmodifiableSet(this.delegate);
	}

	private static final CompoundFishEnv empty = new CompoundFishEnv(List.of());

	@NotNull public static CompoundFishEnv empty(){
		return empty;
	}
}
