package datagen;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import tictim.tfts.contents.TFTSRegistries;
import tictim.tfts.contents.fish.BaitStat;
import tictim.tfts.contents.fish.BaitStatBuilder;
import tictim.tfts.contents.item.Bait;
import tictim.tfts.contents.item.Fish;
import tictim.tfts.contents.item.Thing;

import java.util.Map;

import static datagen.fish.BaitTypes.*;

public class BaitStatGen implements RegistrySetBuilder.RegistryBootstrap<BaitStat>{
	private final Map<Item, BaitStatBuilder> builders = new Object2ObjectOpenHashMap<>();

	protected void registerAll(){
		register(Bait.APPRENTICE_BAIT)
				.stat(ALL_PLANTS, .5)
				.stat(ALL_MEATS, .5);

		register(Bait.JOURNEYMAN_BAIT)
				.stat(ALL_PLANTS, 1)
				.stat(ALL_MEATS, 1);

		register(Bait.MASTER_BAIT)
				.stat(ALL_PLANTS, 1.5)
				.stat(ALL_MEATS, 1.5);

		register(Bait.APPLE_BAIT).stat(APPLE, 1).stat(SUGAR, .5);
		register(Bait.SWEET_BERRY_BAIT).stat(SWEET_BERRY, 1).stat(SUGAR, .7);
		register(Bait.GLOW_BERRY_BAIT).stat(GLOW_BERRY, 1).stat(SUGAR, .1);
		register(Bait.MELON_BAIT).stat(MELON, 1).stat(SUGAR, .1).stat(ALL_PLANTS, .5);
		register(Bait.PUMPKIN_BAIT).stat(PUMPKIN, 1).stat(SUGAR, .2);

		register(Bait.BEETROOT_BAIT).stat(BEETROOT, 1);
		register(Bait.CARROT_BAIT).stat(CARROT, 1);

		register(Bait.SEED_BAIT).stat(SEED, 1).stat(ALL_PLANTS, .25);
		register(Bait.FLOWER_BAIT).stat(FLOWER, 1).stat(ALL_PLANTS, .25);

		register(Bait.GOLDEN_APPLE_BAIT).stat(APPLE, 1).stat(SUGAR, .5).stat(GOLD, 1.5);
		register(Bait.GOLDEN_CARROT_BAIT).stat(CARROT, 1).stat(GOLD, .5);
		register(Bait.GLISTERING_MELON_BAIT).stat(MELON, 1).stat(SUGAR, .1).stat(ALL_PLANTS, .5).stat(GOLD, .5);
		register(Bait.ENCHANTED_GOLDEN_APPLE_BAIT).stat(APPLE, 1).stat(SUGAR, .5).stat(GOLD, 20);

		register(Bait.WORM).stat(WORM_MEAT, 1);
		register(Bait.GOLDEN_WORM).stat(WORM_MEAT, 2.5).stat(GOLD, 2.5);

		register(Fish.SHRIMP).stat(FISH_MEAT, 1.75);

		register(Thing.SMALL_FISH_FILLET).stat(FISH_MEAT, 1).stat(ALL_FOODS, 0.25);
		register(Thing.COOKED_SMALL_FISH_FILLET).stat(FISH_MEAT, 0.75).stat(ALL_FOODS, 1);

		register(Thing.FISH_FILLET).stat(FISH_MEAT, 1.5).stat(ALL_FOODS, 1);
		register(Thing.COOKED_FISH_FILLET).stat(FISH_MEAT, 1.25).stat(ALL_FOODS, 1.5);

		register(Items.BROWN_MUSHROOM).stat(BROWN_MUSHROOM, 1).stat(ALL_PLANTS, .25);
		register(Items.RED_MUSHROOM).stat(RED_MUSHROOM, 1).stat(ALL_PLANTS, .25);

		register(Items.SPIDER_EYE).stat(SPIDER, 1).stat(INSECT_MEAT, 1);
		register(Items.FERMENTED_SPIDER_EYE).stat(SPIDER, .75).stat(INSECT_MEAT, .75).stat(SUGAR, .25).stat(BROWN_MUSHROOM, .25);
	}

	@Override public final void run(@NotNull BootstapContext<BaitStat> context){
		registerAll();

		for(var e : builders.entrySet()){
			ResourceLocation id = ForgeRegistries.ITEMS.getKey(e.getKey());
			if(id==null) throw new IllegalStateException("Cannot get ID of item instance "+e.getKey());

			context.register(
					ResourceKey.create(TFTSRegistries.BAIT_STAT_REGISTRY_KEY, id),
					e.getValue().create());
		}
	}

	protected BaitStatBuilder register(@NotNull ItemLike item){
		BaitStatBuilder builder = new BaitStatBuilder();
		if(builders.put(item.asItem(), builder)!=null){
			throw new IllegalStateException("Already registered");
		}
		return builder;
	}
}
