package tictim.tfts.contents.fish;

import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.*;

public enum PrimitiveFishEnv implements FishEnv, StringRepresentable{
	OCEAN_SURFACE(Biomes.OCEAN),
	OCEAN_DEEP(Biomes.DEEP_OCEAN),
	OCEAN_WARM(Biomes.WARM_OCEAN), // warm ocean has no deep variant
	OCEAN_LUKEWARM_SURFACE(Biomes.LUKEWARM_OCEAN),
	OCEAN_LUKEWARM_DEEP(Biomes.DEEP_LUKEWARM_OCEAN),
	OCEAN_COLD_SURFACE(Biomes.COLD_OCEAN),
	OCEAN_COLD_DEEP(Biomes.DEEP_COLD_OCEAN),
	OCEAN_FROZEN_SURFACE(Biomes.FROZEN_OCEAN),
	OCEAN_FROZEN_DEEP(Biomes.DEEP_FROZEN_OCEAN),

	FRESHWATER; // TODO more non-ocean environments to be added

	private final String name = name().toLowerCase(Locale.ROOT);
	private final Set<ResourceKey<Biome>> biomes;

	PrimitiveFishEnv(ResourceKey<Biome>... biomes){
		this.biomes = new ObjectOpenHashSet<>(biomes);
	}

	@NotNull public Set<ResourceKey<Biome>> biomes(){
		return this.biomes;
	}

	@Override @NotNull @Unmodifiable public Set<PrimitiveFishEnv> asSet(){
		return Set.of(this);
	}
	@Override @NotNull public String getSerializedName(){
		return this.name;
	}

	public static final Codec<PrimitiveFishEnv> CODEC = StringRepresentable.fromEnum(PrimitiveFishEnv::values);
}
