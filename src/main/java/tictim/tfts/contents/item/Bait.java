package tictim.tfts.contents.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tictim.tfts.contents.TFTSRegistries;

import java.util.Locale;
import java.util.function.Function;
import java.util.function.Supplier;

public enum Bait implements ItemLike{
	APPRENTICE_BAIT,
	JOURNEYMAN_BAIT,
	MASTER_BAIT,

	// apple

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
	ENCHANTED_GOLDEN_APPLE_BAIT(p -> new Item(p){
		@Override public boolean isFoil(@NotNull ItemStack stack){
			return true;
		}
	}),

	// insects

	WORM,
	GOLDEN_WORM,

	// weird shit

	ROTTEN_FLESH_BAIT,
	BONE_MEAL_BAIT;

	private final String registryName = name().toLowerCase(Locale.ROOT);
	private final Supplier<Item.Properties> propertyFactory;
	private final Function<Item.Properties, Item> itemFactory;
	private RegistryObject<Item> item;

	Bait(){
		this(null, null);
	}
	Bait(@Nullable Supplier<Item.Properties> propertyFactory){
		this(propertyFactory, null);
	}
	Bait(@Nullable Function<Item.Properties, Item> itemFactory){
		this(null, itemFactory);
	}
	Bait(@Nullable Supplier<Item.Properties> propertyFactory, @Nullable Function<Item.Properties, Item> itemFactory){
		this.propertyFactory = propertyFactory;
		this.itemFactory = itemFactory;
	}

	private Item createItem(){
		Item.Properties p = this.propertyFactory==null ? new Item.Properties() : this.propertyFactory.get();
		return this.itemFactory==null ? new Item(p) : this.itemFactory.apply(p);
	}

	@NotNull public String registryName(){
		return registryName;
	}

	@Override @NotNull public Item asItem(){
		if(this.item==null) throw new IllegalStateException("Item not registered");
		return this.item.get();
	}

	public static void register(){
		for(var e : values()){
			if(e.item!=null) throw new IllegalStateException("Trying to register fish twice");
			e.item = TFTSRegistries.ITEMS.register(e.registryName, e::createItem);
		}
	}
}
