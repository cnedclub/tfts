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
import tictim.tfts.contents.fish.TrashAnglingEntry;
import tictim.tfts.contents.fish.condition.LightCondition;
import tictim.tfts.contents.fish.condition.TimeCondition;
import tictim.tfts.contents.item.Fish;

import java.util.Map;

import static datagen.fish.BaitTypes.*;
import static datagen.fish.FishUtils.*;
import static tictim.tfts.TFTSMod.id;
import static tictim.tfts.contents.fish.FishEnvs.*;
import static tictim.tfts.contents.fish.NibbleBehavior.nibble;
import static tictim.tfts.contents.fish.NibbleBehavior.snatch;
import static tictim.tfts.contents.fish.PrimitiveFishEnv.*;

public class AnglingEntryGen implements RegistrySetBuilder.RegistryBootstrap<AnglingEntry<?>> {
	private final Map<ResourceLocation, AnglingEntryBuilder<?>> map = new Object2ObjectOpenHashMap<>();

	protected void registerAll() {
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

		// bass

		simpleFish(Fish.BASS)
				.baseWeight(50)
				.weightGrowth(0)
				.minFishingPower(0.1)
				.env(ALL_WATER)
				.bait(any(ALL_PLANTS, ALL_MUSHROOMS, ALL_MEATS, ALL_FOODS), 1)
				.nibbleBehavior(nibble(.5));

		// common

		simpleFish(Fish.CRAB)
				.baseWeight(40)
				.minFishingPower(.5)
				.env(OCEAN, OCEAN_WARM)
				.bait(any(ALL_MEATS, ALL_FOODS), 1)
				.nibbleBehavior(nibble(.5));

		simpleFish(Fish.CATFISH)
				.baseWeight(40)
				.minFishingPower(.5)
				.env(UNKNOWN)
				.bait(any(ALL_MEATS), 1)
				.nibbleBehavior(nibble(.5));

		simpleFish(Fish.BROWN_CROAKER)
				.baseWeight(40)
				.minFishingPower(.5)
				.env(OCEAN)
				.bait(any(WORM_MEAT), 1)
				.nibbleBehavior(nibble(.5));

		simpleFish(Fish.SHRIMP)
				.baseWeight(40)
				.minFishingPower(.5)
				.env(UNKNOWN, OCEAN_COLD, OCEAN)
				.bait(ALL_FOODS, 1)
				.nibbleBehavior(snatch());//TEOKBAP

		simpleFish(Fish.ROCKFISH)
				.baseWeight(40)
				.minFishingPower(.5)
				.env(OCEAN_LUKEWARM, OCEAN)
				.bait(any(FISH_MEAT), 1)
				.nibbleBehavior(nibble(.5));

		simpleFish(Fish.CARP)
				.baseWeight(40)
				.minFishingPower(.5)
				.env(UNKNOWN)
				.bait(any(WORM_MEAT, ALL_PLANTS), 1)
				.nibbleBehavior(nibble(.5));

		simpleFish(Fish.CREEPER_FISH)
				.baseWeight(40)
				.minFishingPower(.5)
				.env(ALL_WATER)
				.condition(LightCondition.EIGHT)
				.bait(any(ALL_MEATS, ALL_FOODS, ALL_MUSHROOMS, ALL_PLANTS), 1)
				.nibbleBehavior(nibble(.3));

		simpleFish(Fish.BARRELEYE)
				.baseWeight(40)
				.minFishingPower(.5)
				.env(OCEAN_DEEP, OCEAN_COLD_DEEP)
				.condition(TimeCondition.NIGHT)
				.bait(FISH_MEAT, 1)
				.nibbleBehavior(nibble(.3));

		simpleFish(Fish.ZOMFISH)
				.baseWeight(40)
				.minFishingPower(.5)
				.env(ALL_WATER)
				.condition(LightCondition.EIGHT)
				.condition(TimeCondition.NIGHT)
				.bait(any(ALL_MEATS, ALL_FOODS, ALL_MUSHROOMS, ALL_PLANTS), 1)
				.nibbleBehavior(snatch());


		// uncommon

		simpleFish(Fish.GARIBALDI_DAMSELFISH)
				.baseWeight(30)
				.minFishingPower(.5)
				.env(OCEAN_LUKEWARM, OCEAN_WARM)
				.bait(max(c(BREAD, 1.2), c(WORM_MEAT, 1)))
				.nibbleBehavior(snatch());

		simpleFish(Fish.LOBSTER)
				.baseWeight(30)
				.minFishingPower(.5)
				.env(OCEAN)
				.bait(max(c(FISH_MEAT, 1), c(BREAD, .8)))
				.nibbleBehavior(nibble(.3));

		simpleFish(Fish.TUNA)
				.baseWeight(30)
				.minFishingPower(.5)
				.env(ALL_OCEANS)
				.bait(FISH_MEAT, 1)
				.nibbleBehavior(nibble(.2));

		simpleFish(Fish.FLYING_FISH)
				.baseWeight(30)
				.minFishingPower(0.5)
				.env(OCEAN)
				.bait(INSECT_MEAT, 1)
				.nibbleBehavior(snatch());

		simpleFish(Fish.SEAHORSE)
				.baseWeight(30)
				.minFishingPower(0.5)
				.env(OCEAN_SURFACE, OCEAN_WARM)
				.bait(max(c(ALL_PLANTS, 0.5), c(INSECT_MEAT, 1)))
				.nibbleBehavior(nibble(.3));

		// Rare

		simpleFish(Fish.OARFISH)
				.baseWeight(20)
				.minFishingPower(.5)
				.env(OCEAN_LUKEWARM_DEEP)
				.bait(FISH_MEAT, 1)
				.nibbleBehavior(nibble(.2));

		simpleFish(Fish.MELIBE)
				.baseWeight(20)
				.minFishingPower(.5)
				.env(OCEAN_LUKEWARM)
				.bait(max(c(WORM_MEAT, .5), c(INSECT_MEAT, .5)))
				.nibbleBehavior(nibble(.2));

		simpleFish(Fish.MARLIN)
				.baseWeight(20)
				.minFishingPower(.5)
				.env(OCEAN)
				.bait(FISH_MEAT, 1)
				.nibbleBehavior(nibble(.3));

		simpleFish(Fish.PENGUIN)
				.baseWeight(20)
				.minFishingPower(1)
				.env(OCEAN_COLD, OCEAN_FROZEN)
				.bait(FISH_MEAT, 1)
				.nibbleBehavior(snatch());

		trash(id("water_trash"))
				.baseWeight(10)
				.weightGrowth(-10)
				.minWeight(1)
				.env(ALL_WATER);
	}

	@Override
	public void run(@NotNull BootstapContext<AnglingEntry<?>> ctx) {
		registerAll();

		for (var e : this.map.entrySet()) {
			ctx.register(ResourceKey.create(TFTSRegistries.ANGLING_ENTRY_REGISTRY_KEY, e.getKey()),
					e.getValue().create());
		}
	}

	protected AnglingEntryBuilder<SimpleAnglingEntry> simpleFish(@NotNull Fish fish) {
		return simpleFish(id(fish.registryName())).loot(fish);
	}

	protected AnglingEntryBuilder<SimpleAnglingEntry> simpleFish(@NotNull ResourceLocation id) {
		return register(id, AnglingEntryBuilder.simpleFish());
	}

	protected AnglingEntryBuilder<TrashAnglingEntry> trash(@NotNull ResourceLocation id) {
		return register(id, AnglingEntryBuilder.trash());
	}

	protected <T extends AnglingEntryBuilder<?>> T register(@NotNull ResourceLocation id, @NotNull T entry) {
		if (this.map.put(id, entry) != null) {
			throw new IllegalStateException("Angling entry with ID " + id + " already exists");
		}
		return entry;
	}
}
