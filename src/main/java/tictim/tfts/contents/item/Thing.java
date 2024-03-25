package tictim.tfts.contents.item;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;
import tictim.tfts.contents.TFTSBlocks;
import tictim.tfts.contents.TFTSMenus;
import tictim.tfts.contents.TFTSRegistries;

import java.util.Locale;
import java.util.function.Supplier;

import static tictim.tfts.contents.item.factories.ItemFactories.*;

public enum Thing implements ItemLike{
	BAIT_BOX(custom(p -> new BaitBoxItem(3, TFTSMenus.BAIT_BOX, p))),
	TROWEL(custom(TrowelItem::new)),
	COOKING_MORTAR(custom(() -> p().defaultDurability(64), CookingMortarItem::new)),

	FILLET_TABLE(block(TFTSBlocks.FILLET_TABLE)),

	SMALL_FISH_FILLET(food(1)),
	COOKED_SMALL_FISH_FILLET(food(4)),
	FISH_FILLET(food(3)),
	COOKED_FISH_FILLET(food(6)),

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

	private final RegistryObject<Item> item;

	Thing(){
		this(simple());
	}
	Thing(@NotNull Supplier<@NotNull Item> itemSupplier){
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
