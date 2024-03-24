package tictim.tfts.contents.item;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tictim.tfts.TFTSMod;
import tictim.tfts.contents.TFTSBlocks;
import tictim.tfts.contents.TFTSMenus;
import tictim.tfts.contents.TFTSRegistries;

import java.util.Locale;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public enum Thing implements ItemLike{
	BAIT_BOX(p -> new BaitBoxItem(3, TFTSMenus.BAIT_BOX, p)),
	TROWEL(TrowelItem::new),
	COOKING_MORTAR(() -> new Item.Properties().defaultDurability(64), CookingMortarItem::new),

	FILLET_TABLE(p -> new BlockItem(TFTSBlocks.FILLET_TABLE.get(), p)),

	SMALL_FISH_FILLET(simpleFood(1)),
	COOKED_SMALL_FISH_FILLET(simpleFood(4)),
	FISH_FILLET(simpleFood(3)),
	COOKED_FISH_FILLET(simpleFood(6)),

	STARCH,

	JAJO_COLA(food(b -> b.nutrition(1).saturationMod(0)
			.effect(() -> new MobEffectInstance(MobEffects.BLINDNESS, 200), 1)
			.effect(() -> new MobEffectInstance(MobEffects.CONFUSION, 200), 1)
			.effect(() -> new MobEffectInstance(MobEffects.POISON, 100), 1)
			.effect(() -> new MobEffectInstance(MobEffects.WEAKNESS, 200), 1)
			.effect(() -> new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 200), 1)
	)),

	BARRELEYE_EYEBALL(food(b -> b.nutrition(1)
			.effect(() -> new MobEffectInstance(MobEffects.NIGHT_VISION, 600), 1)
			.alwaysEat()
	));

	private final String registryName = name().toLowerCase(Locale.ROOT);
	private final Supplier<Item.Properties> propertyFactory;
	private final Function<Item.Properties, Item> itemFactory;
	private RegistryObject<Item> item;

	Thing(){
		this(null, null);
	}
	Thing(@Nullable Supplier<Item.Properties> propertyFactory){
		this(propertyFactory, null);
	}
	Thing(@Nullable Function<Item.Properties, Item> itemFactory){
		this(null, itemFactory);
	}
	Thing(@Nullable Supplier<Item.Properties> propertyFactory, @Nullable Function<Item.Properties, Item> itemFactory){
		this.propertyFactory = propertyFactory;
		this.itemFactory = itemFactory;
	}

	private static Supplier<Item.Properties> simpleFood(int nutrition){
		return () -> new Item.Properties().food(new FoodProperties.Builder().nutrition(nutrition).build());
	}

	private static Supplier<Item.Properties> food(Consumer<FoodProperties.Builder> buildAction){
		return () -> {
			FoodProperties.Builder builder = new FoodProperties.Builder();
			buildAction.accept(builder);
			return new Item.Properties().food(builder.build());
		};
	}

	private Item createItem(){
		Item.Properties p = this.propertyFactory==null ? new Item.Properties() : this.propertyFactory.get();
		return this.itemFactory==null ? new Item(p) : this.itemFactory.apply(p);
	}

	@NotNull public String registryName(){
		return registryName;
	}

	@NotNull public ResourceLocation registryID(){
		return TFTSMod.id(registryName());
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
