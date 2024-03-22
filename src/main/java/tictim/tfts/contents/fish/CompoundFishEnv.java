package tictim.tfts.contents.fish;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.*;
import java.util.stream.Collectors;

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

	@Override public boolean equals(Object o){
		if(this==o) return true;
		if(o==null||getClass()!=o.getClass()) return false;
		CompoundFishEnv that = (CompoundFishEnv)o;
		return Objects.equals(delegate, that.delegate);
	}
	@Override public int hashCode(){
		return Objects.hash(delegate);
	}
	@Override public String toString(){
		return "["+delegate.stream().map(PrimitiveFishEnv::getSerializedName).collect(Collectors.joining(", "))+"]";
	}

	private static final CompoundFishEnv empty = new CompoundFishEnv(List.of());

	@NotNull public static CompoundFishEnv empty(){
		return empty;
	}
}
