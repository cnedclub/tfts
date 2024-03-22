package tictim.tfts.contents.fish;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;
import java.util.Set;

public sealed interface FishEnv permits CompoundFishEnv, PrimitiveFishEnv{
	@NotNull @Unmodifiable Set<@NotNull PrimitiveFishEnv> asSet();

	@NotNull static FishEnv of(@NotNull FishEnv @NotNull ... environments){
		return switch(environments.length){
			case 0 -> CompoundFishEnv.empty();
			case 1 -> environments[0];
			default -> new CompoundFishEnv(environments);
		};
	}

	@NotNull static FishEnv of(@NotNull List<? extends FishEnv> environments){
		return switch(environments.size()){
			case 0 -> CompoundFishEnv.empty();
			case 1 -> environments.get(0);
			default -> new CompoundFishEnv(environments);
		};
	}

	Codec<FishEnv> CODEC = Codec.either(PrimitiveFishEnv.CODEC, PrimitiveFishEnv.CODEC.listOf())
			.xmap(e -> e.map(a -> a, FishEnv::of),
					e -> e instanceof PrimitiveFishEnv p ? Either.left(p) : Either.right(List.copyOf(e.asSet())));
}
