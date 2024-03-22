package tictim.tfts.contents.fish;

import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraftforge.event.OnDatapackSyncEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;
import tictim.tfts.TFTSMod;

import java.util.*;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;

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
	UNKNOWN(20, 100, (b, r) -> !is(b, r, BiomeTags.IS_OCEAN)&&!is(b, r, BiomeTags.IS_BEACH));

	private final String name = name().toLowerCase(Locale.ROOT);
	private final Set<Biome> biomes = new ObjectOpenHashSet<>();

	private final long minimumPoolSize;
	private final long poolSizeReference;
	private final BiPredicate<Biome, Registry<Biome>> biomeTest;

	@SafeVarargs PrimitiveFishEnv(long minimumPoolSize, long poolSizeReference, ResourceKey<Biome>... biomes){
		this(minimumPoolSize, poolSizeReference, (b, r) -> Arrays.stream(biomes).anyMatch(biomeKey -> is(b, r, biomeKey)));
	}

	PrimitiveFishEnv(long minimumPoolSize, long poolSizeReference, TagKey<Biome> tag){
		this(minimumPoolSize, poolSizeReference, (b, r) -> is(b, r, tag));
	}

	PrimitiveFishEnv(long minimumPoolSize, long poolSizeReference, BiPredicate<Biome, Registry<Biome>> biomeTest){
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

	private static boolean is(Biome biome, Registry<Biome> registry, ResourceKey<Biome> key){
		return Objects.equals(registry.getKey(biome), key.location());
	}
	private static boolean is(Biome biome, Registry<Biome> registry, TagKey<Biome> tag){
		var tf = registry.getTag(tag);
		return tf.isPresent()&&tf.get().contains(Holder.direct(biome));
	}

	@SubscribeEvent
	public static void onServerStarted(ServerStartedEvent event){
		updateBiomeMap(event.getServer().registryAccess());
	}

	@SubscribeEvent
	public static void onDatapackSync(OnDatapackSyncEvent event){
		if(event.getPlayer()!=null) return;
		updateBiomeMap(event.getPlayerList().getServer().registryAccess());
	}

	public static void updateBiomeMap(@NotNull RegistryAccess registryAccess){
		for(PrimitiveFishEnv e : values()) e.biomes.clear();

		Optional<Registry<Biome>> o = registryAccess.registry(Registries.BIOME);
		if(o.isEmpty()) return;

		// assign biome to one fish env, to prevent one biome affecting two primitive environments
		Registry<Biome> registry = o.get();
		for(Biome biome : registry){
			for(PrimitiveFishEnv e : values()){
				if(e.biomeTest.test(biome, registry)){
					e.biomes.add(biome);
					break;
				}
			}
		}

		for(PrimitiveFishEnv e : values()){
			TFTSMod.LOGGER.info("{} : [{}]", e, e.biomes.stream()
					.map(b -> registry.getKey(b)+"")
					.collect(Collectors.joining(", ")));
		}
	}
}
