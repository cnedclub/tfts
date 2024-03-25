package tictim.tfts.contents.item.factories;

import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public interface ItemFactories{
	@NotNull static Supplier<@NotNull Item> simple(){
		return SimpleItemFactory.INSTANCE;
	}

	@NotNull static Supplier<@NotNull Item> custom(@Nullable Supplier<Item.Properties> property){
		return new CustomItemFactory(property);
	}

	@NotNull static Supplier<@NotNull Item> custom(@Nullable Function<Item.Properties, ? extends Item> item){
		return new CustomItemFactory(item);
	}

	@NotNull static Supplier<@NotNull Item> custom(@Nullable Supplier<Item.Properties> property,
	                                               @Nullable Function<Item.Properties, ? extends Item> item){
		return new CustomItemFactory(property, item);
	}

	@NotNull static Supplier<@NotNull Item> food(int nutrition){
		return new FoodItemFactory(nutrition);
	}

	@NotNull static Supplier<@NotNull Item> food(int nutrition, float saturationMod){
		return new FoodItemFactory(nutrition, saturationMod);
	}

	@NotNull static Supplier<@NotNull Item> food(@NotNull Consumer<FoodProperties.Builder> foodProperty){
		return new FoodItemFactory(foodProperty);
	}

	@NotNull static Supplier<@NotNull Item> food(@NotNull Consumer<FoodProperties.Builder> foodProperty,
	                                             @Nullable Function<Item.Properties, ? extends Item> item){
		return new FoodItemFactory(foodProperty, item);
	}

	@NotNull static Supplier<@NotNull Item> food(@NotNull Consumer<FoodProperties.Builder> foodProperty,
	                                             @Nullable Supplier<Item.Properties> property,
	                                             @Nullable Function<Item.Properties, ? extends Item> item){
		return new FoodItemFactory(foodProperty, property, item);
	}

	@NotNull static Supplier<@NotNull Item> block(@NotNull Supplier<@NotNull Block> block){
		return new BlockItemFactory(block);
	}
	@NotNull static Supplier<@NotNull Item> block(@NotNull Supplier<@NotNull Block> block,
	                                              @Nullable Supplier<Item.Properties> property){
		return new BlockItemFactory(block, property);
	}
	@NotNull static Supplier<@NotNull Item> block(@NotNull Supplier<@NotNull Block> block,
	                                              @Nullable BiFunction<Block, Item.Properties, ? extends BlockItem> item){
		return new BlockItemFactory(block, item);
	}
	@NotNull static Supplier<@NotNull Item> block(@NotNull Supplier<@NotNull Block> block,
	                                              @Nullable Supplier<Item.Properties> property,
	                                              @Nullable BiFunction<Block, Item.Properties, ? extends BlockItem> item){
		return new BlockItemFactory(block, property, item);
	}

	// helper method

	@NotNull static Item.Properties p(){
		return new Item.Properties();
	}
}
