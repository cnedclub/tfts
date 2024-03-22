package tictim.tfts.contents.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;
import tictim.tfts.contents.TFTSRegistries;

import java.util.Locale;

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
	MARLIN,
	GARIBALDI_DAMSELFISH,
	SEAHORSE,

	// rare fish

	// rare rare fish

	OCEAN_SUNFISH,


	// not exactly a fish
	LOBSTER,
	SHRIMP,

	// shit
	BROWN_CROAKER,
	;

	private final String registryName = name().toLowerCase(Locale.ROOT);
	private RegistryObject<Item> item;

	@NotNull public String registryName(){
		return registryName;
	}

	@Override @NotNull public Item asItem(){
		if(this.item==null) throw new IllegalStateException("Item not registered");
		return this.item.get();
	}

	public static void register(){
		for(Fish fish : values()){
			if(fish.item!=null) throw new IllegalStateException("Trying to register fish twice");
			fish.item = TFTSRegistries.ITEMS.register(fish.registryName, () -> new Item(new Item.Properties()));
		}
	}
}
