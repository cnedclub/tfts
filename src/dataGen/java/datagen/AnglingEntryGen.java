package datagen;

import datagen.fish.AnglingEntryBuilder;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;
import tictim.tfts.contents.TFTSRegistries;
import tictim.tfts.contents.fish.AnglingEntry;
import tictim.tfts.contents.fish.SimpleAnglingEntry;
import tictim.tfts.contents.item.Fish;

import java.util.Map;

import static datagen.fish.BaitTypes.*;
import static datagen.fish.FishUtils.*;
import static tictim.tfts.TFTSMod.id;
import static tictim.tfts.contents.fish.FishEnvs.*;
import static tictim.tfts.contents.fish.NibbleBehavior.nibble;
import static tictim.tfts.contents.fish.NibbleBehavior.snatch;
import static tictim.tfts.contents.fish.PrimitiveFishEnv.*;

public class AnglingEntryGen implements RegistrySetBuilder.RegistryBootstrap<AnglingEntry<?>>{
	private final Map<ResourceLocation, AnglingEntryBuilder<?>> map = new Object2ObjectOpenHashMap<>();

	protected void registerAll(){
		// vanilla fish

		simpleFish(id("cod"))
				.loot(Items.COD)
				.baseWeight(50)
				.minFishingPower(0.5)
				.env(OCEAN, OCEAN_LUKEWARM, OCEAN_COLD)
				.bait(max(c(ALL_MEATS, 1), c(FISH_MEAT, 1.5)))
				.nibbleBehavior(snatch());

		simpleFish(id("salmon"))
				.loot(Items.SALMON)
				.baseWeight(50)
				.minFishingPower(0.5)
				.env(OCEAN_COLD, OCEAN_FROZEN, UNKNOWN)
				.bait(ALL_MEATS, 1)
				.nibbleBehavior(nibble(.5));

		simpleFish(id("pufferfish"))
				.loot(Items.PUFFERFISH)
				.baseWeight(30)
				.minFishingPower(0.75)
				.env(OCEAN_WARM)
				.bait(max(c(any(ALL_PLANTS, ALL_MUSHROOMS, ALL_MEATS), 1), c(CARROT, 2)))
				.nibbleBehavior(nibble(.5));

		simpleFish(id("tropical_fish"))
				.loot(Items.TROPICAL_FISH)
				.baseWeight(50)
				.minFishingPower(0.5)
				.env(OCEAN_WARM, OCEAN_LUKEWARM)
				.bait(ALL_PLANTS, 1)
				.nibbleBehavior(nibble(.5));

		// trash

		simpleFish(Fish.BASS)
				.baseWeight(50)
				.weightGrowth(0)
				.minFishingPower(0.1)
				.env(ALL_WATER)
				.bait(any(ALL_PLANTS, ALL_MUSHROOMS, ALL_MEATS, ALL_FOODS), 1)
				.nibbleBehavior(nibble(.5));

		// common


		// uncommon

		simpleFish(Fish.FLYING_FISH)
				.baseWeight(50)
				.minFishingPower(0.5)
				.env(OCEAN)
				.bait(INSECT_MEAT, 1)
				.nibbleBehavior(snatch());

		simpleFish(Fish.SEAHORSE)
				.baseWeight(50)
				.minFishingPower(0.5)
				.env(OCEAN_SURFACE, OCEAN_WARM)
				.bait(max(c(ALL_PLANTS, 0.5), c(INSECT_MEAT, 1)))
				.nibbleBehavior(nibble(.3));


	}

	@Override public void run(@NotNull BootstapContext<AnglingEntry<?>> ctx){
		registerAll();

		for(var e : this.map.entrySet()){
			ctx.register(ResourceKey.create(TFTSRegistries.ANGLING_ENTRY_REGISTRY_KEY, e.getKey()),
					e.getValue().create());
		}
	}

	protected AnglingEntryBuilder<SimpleAnglingEntry> simpleFish(@NotNull Fish fish){
		return simpleFish(id(fish.registryName())).loot(fish);
	}
	protected AnglingEntryBuilder<SimpleAnglingEntry> simpleFish(@NotNull ResourceLocation id){
		return register(id, AnglingEntryBuilder.simpleFish());
	}

	protected <T extends AnglingEntryBuilder<?>> T register(@NotNull ResourceLocation id, @NotNull T entry){
		if(this.map.put(id, entry)!=null){
			throw new IllegalStateException("Angling entry with ID "+id+" already exists");
		}
		return entry;
	}
}
