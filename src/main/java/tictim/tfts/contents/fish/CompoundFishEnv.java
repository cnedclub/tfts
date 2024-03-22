package tictim.tfts.contents.fish;

import com.mojang.serialization.Codec;
import net.minecraft.util.ExtraCodecs;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public final class CompoundFishEnv implements FishEnv{
	private final List<FishEnv> rawList;

	private Set<PrimitiveFishEnv> delegate;

	public CompoundFishEnv(@NotNull List<@NotNull FishEnv> rawList){
		this.rawList = rawList;
	}

	@Override @NotNull @Unmodifiable public Set<@NotNull PrimitiveFishEnv> asSet(){
		if(this.delegate==null){
			this.delegate = this.rawList.stream()
					.flatMap(e -> e.asSet().stream())
					.collect(Collectors.toUnmodifiableSet());
		}
		return this.delegate;
	}

	public static final Codec<CompoundFishEnv> CODEC = ExtraCodecs.lazyInitializedCodec(() ->
			FishEnv.CODEC.listOf().xmap(CompoundFishEnv::new, e -> e.rawList));
}
