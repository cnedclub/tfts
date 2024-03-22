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
import tictim.tfts.contents.fish.*;

import java.util.Map;

import static datagen.fish.FishUtils.*;
import static tictim.tfts.TFTSMod.id;
import static tictim.tfts.contents.fish.BaitTypes.*;
import static tictim.tfts.contents.fish.NibbleBehavior.nibble;
import static tictim.tfts.contents.fish.NibbleBehavior.snatch;
import static tictim.tfts.contents.fish.PrimitiveFishEnv.*;
import static tictim.tfts.contents.fish.FishEnvs.*;

public class AnglingEntryGen implements RegistrySetBuilder.RegistryBootstrap<AnglingEntry<?>>{
	private final Map<ResourceLocation, AnglingEntryBuilder<?>> map = new Object2ObjectOpenHashMap<>();

	protected void registerAll(){
		/*
		register(id("test1"), new SimpleAnglingEntry(
				10,
				List.of(new ItemStack(Items.COBBLESTONE)),
				NibbleBehavior.none(),
				0,
				false));
		register(id("test2"), new SimpleAnglingEntry(
				10,
				List.of(new ItemStack(Items.ACACIA_SLAB)),
				NibbleBehavior.snatch(),
				.1,
				true));
		register(id("test3"), new SimpleAnglingEntry(
				10,
				List.of(new ItemStack(Items.BEE_SPAWN_EGG)),
				NibbleBehavior.nibble(0.5),
				.1,
				true));
		 */

		// vanilla fish

		simpleFish(new ResourceLocation("cod"))
				.loot(Items.COD)
				.weight(50)
				.env(OCEAN, OCEAN_LUKEWARM, OCEAN_COLD)
				.bait(max(c(ALL_MEATS, 1), c(FISH_MEAT, 1.5)))
				.nibbleBehavior(snatch());

		simpleFish(new ResourceLocation("salmon"))
				.loot(Items.SALMON)
				.weight(50)
				.env(OCEAN_COLD, OCEAN_FROZEN, FRESHWATER)
				.bait(ALL_MEATS, 1)
				.nibbleBehavior(nibble(.5));

		simpleFish(new ResourceLocation("pufferfish"))
				.loot(Items.PUFFERFISH)
				.weight(30)
				.env(OCEAN_WARM)
				.bait(any(ALL_PLANTS, ALL_MUSHROOMS, ALL_MEATS), 1)
				.nibbleBehavior(nibble(.5));

		simpleFish(new ResourceLocation("tropical_fish"))
				.loot(Items.TROPICAL_FISH)
				.weight(50)
				.env(OCEAN_WARM, OCEAN_LUKEWARM)
				.bait(ALL_PLANTS, 1)
				.nibbleBehavior(nibble(.5));

		// trash

		simpleFish(Fish.BASS)
				.weight(50)
				.env(ALL_WATER)
				.envBonus(0)
				.bait(any(ALL_PLANTS, ALL_MUSHROOMS, ALL_MEATS, ALL_FOODS), 1)
				.nibbleBehavior(nibble(.5));

		// common


		// uncommon

		simpleFish(Fish.FLYING_FISH)
				.weight(50)
				.env(OCEAN)
				.bait(INSECT_MEAT, 1)
				.nibbleBehavior(snatch());

		simpleFish(Fish.SEAHORSE)
				.weight(50)
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
