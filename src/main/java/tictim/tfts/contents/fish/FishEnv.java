package tictim.tfts.contents.fish;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;
import java.util.Set;
import java.util.function.Function;

public sealed interface FishEnv permits CompoundFishEnv, PrimitiveFishEnv{
	@NotNull @Unmodifiable Set<@NotNull PrimitiveFishEnv> asSet();

	@NotNull static FishEnv combine(@NotNull FishEnv @NotNull ... environments){
		return environments.length==1 ? environments[0] : new CompoundFishEnv(List.of(environments));
	}

	Codec<FishEnv> CODEC = Codec.either(PrimitiveFishEnv.CODEC, CompoundFishEnv.CODEC)
			.xmap(e -> e.map(Function.identity(), Function.identity()),
					e -> e instanceof PrimitiveFishEnv p ? Either.left(p) : Either.right((CompoundFishEnv)e));
}
