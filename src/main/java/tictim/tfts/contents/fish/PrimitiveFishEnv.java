package tictim.tfts.contents.fish;

import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.tags.ITagManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;
import tictim.tfts.TFTSMod;

import java.util.Arrays;
import java.util.Locale;
import java.util.Set;
import java.util.function.Predicate;

// TODO might be able to move this entire thing to datapack???????????? is it even worth it????????????
// TODO probably need to adjust these magic numbers
@Mod.EventBusSubscriber(modid = TFTSMod.MODID)
public enum PrimitiveFishEnv implements FishEnv, StringRepresentable{
	OCEAN_SURFACE(30, 200, Biomes.OCEAN),
	OCEAN_DEEP(30, 300, Biomes.DEEP_OCEAN),
	OCEAN_WARM(30, 200, Biomes.WARM_OCEAN), // warm ocean has no deep variant
	OCEAN_LUKEWARM_SURFACE(30, 200, Biomes.LUKEWARM_OCEAN),
	OCEAN_LUKEWARM_DEEP(30, 300, Biomes.DEEP_LUKEWARM_OCEAN),
	OCEAN_COLD_SURFACE(30, 200, Biomes.COLD_OCEAN),
	OCEAN_COLD_DEEP(30, 300, Biomes.DEEP_COLD_OCEAN),
	OCEAN_FROZEN_SURFACE(30, 200, Biomes.FROZEN_OCEAN),
	OCEAN_FROZEN_DEEP(30, 300, Biomes.DEEP_FROZEN_OCEAN),

	STONY_SHORE(15, 80, Biomes.STONY_SHORE),

	UNKNOWN_OCEAN(30, 200, BiomeTags.IS_OCEAN),
	UNKNOWN_BEACH(15, 80, BiomeTags.IS_BEACH),

	// TODO more non-ocean environments to be added
	UNKNOWN(20, 100, b -> !is(b, BiomeTags.IS_OCEAN)&&!is(b, BiomeTags.IS_BEACH));

	private final String name = name().toLowerCase(Locale.ROOT);
	private final Set<Biome> biomes = new ObjectOpenHashSet<>();

	private final long minimumPoolSize;
	private final long poolSizeReference;
	private final Predicate<Biome> biomeTest;

	@SafeVarargs PrimitiveFishEnv(long minimumPoolSize, long poolSizeReference, ResourceKey<Biome>... biomes){
		this(minimumPoolSize, poolSizeReference, b -> Arrays.stream(biomes).anyMatch(biomeKey -> is(b, biomeKey)));
	}

	PrimitiveFishEnv(long minimumPoolSize, long poolSizeReference, TagKey<Biome> tag){
		this(minimumPoolSize, poolSizeReference, b -> is(b, tag));
	}

	PrimitiveFishEnv(long minimumPoolSize, long poolSizeReference, Predicate<Biome> biomeTest){
		this.minimumPoolSize = minimumPoolSize;
		this.poolSizeReference = poolSizeReference;
		this.biomeTest = biomeTest;
	}

	@NotNull public Set<Biome> biomes(){
		return this.biomes;
	}

	public double calculateFishingPower(long poolSize){
		if(poolSize<=this.minimumPoolSize) return 0;
		return Math.sqrt((double)(poolSize-this.minimumPoolSize)/(this.poolSizeReference-this.minimumPoolSize));
	}

	@Override @NotNull @Unmodifiable public Set<PrimitiveFishEnv> asSet(){
		return Set.of(this);
	}
	@Override @NotNull public String getSerializedName(){
		return this.name;
	}

	public static final Codec<PrimitiveFishEnv> CODEC = StringRepresentable.fromEnum(PrimitiveFishEnv::values);

	private static boolean is(Biome biome, ResourceKey<Biome> key){
		return biome==ForgeRegistries.BIOMES.getValue(key.location());
	}
	private static boolean is(Biome biome, TagKey<Biome> tag){
		ITagManager<Biome> tags = ForgeRegistries.BIOMES.tags();
		if(tags==null||!tags.isKnownTagName(tag)) return false;
		return tags.getTag(tag).contains(biome);
	}

	@SubscribeEvent
	public static void serverStarted(ServerStartedEvent event){
		for(PrimitiveFishEnv e : values()){
			e.biomes.clear();
		}

		// assign biome to one fish env, to prevent one biome affecting two primitive environments
		for(Biome biome : ForgeRegistries.BIOMES){
			for(PrimitiveFishEnv e : values()){
				if(e.biomeTest.test(biome)){
					e.biomes.add(biome);
					break;
				}
			}
		}
	}
}
