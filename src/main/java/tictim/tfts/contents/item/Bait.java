package tictim.tfts.contents.item;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;
import tictim.tfts.contents.TFTSRegistries;

import java.util.Locale;
import java.util.function.Supplier;

import static tictim.tfts.contents.item.factories.ItemFactories.custom;
import static tictim.tfts.contents.item.factories.ItemFactories.simple;

public enum Bait implements ItemLike{
	APPRENTICE_BAIT,
	JOURNEYMAN_BAIT,
	MASTER_BAIT,

	APPLE_BAIT,
	SWEET_BERRY_BAIT,
	GLOW_BERRY_BAIT,
	MELON_BAIT,
	PUMPKIN_BAIT,

	BEETROOT_BAIT,
	CARROT_BAIT,

	SEED_BAIT,
	FLOWER_BAIT,

	GOLDEN_APPLE_BAIT,
	GOLDEN_CARROT_BAIT,
	GLISTERING_MELON_BAIT,
	ENCHANTED_GOLDEN_APPLE_BAIT(custom(p -> new Item(p){
		@Override public boolean isFoil(@NotNull ItemStack stack){
			return true;
		}
	})),

	WORM,
	GOLDEN_WORM;

	private final RegistryObject<Item> item;

	Bait(){
		this(simple());
	}
	Bait(@NotNull Supplier<@NotNull Item> itemSupplier){
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
