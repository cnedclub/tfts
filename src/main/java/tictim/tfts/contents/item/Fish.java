package tictim.tfts.contents.item;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;
import tictim.tfts.contents.TFTSRegistries;

import java.util.Locale;
import java.util.function.Supplier;

import static tictim.tfts.contents.item.factories.ItemFactories.simple;

// it's plural
public enum Fish implements ItemLike{
	// bass
	BASS,

	// common fish
	ROCKFISH,
	ZOMFISH,
	CREEPER_FISH,
	CRAB,
	BARRELEYE,

	CATFISH, // freshwater
	CARP,    // freshwater

	// uncommon fish

	FLYING_FISH,
	TUNA,

	GARIBALDI_DAMSELFISH,
	SEAHORSE,

	// rare fish

	OARFISH,

	MELIBE,

	MARLIN,

	PENGUIN,

	// rare rare fish

	OCEAN_SUNFISH,


	// not exactly a fish
	LOBSTER,
	SHRIMP,

	// shit
	BROWN_CROAKER;

	private final RegistryObject<Item> item;

	Fish(){
		this(simple());
	}
	Fish(@NotNull Supplier<@NotNull Item> itemSupplier){
		this.item = TFTSRegistries.ITEMS.register(name().toLowerCase(Locale.ROOT), itemSupplier);
	}

	@NotNull public ResourceLocation registryID(){
		return this.item.getId();
	}
	@Override @NotNull public Item asItem(){
		return this.item.get();
	}

	public static void init(){}
}
