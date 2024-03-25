package tictim.tfts.contents.item.factories;

import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class FoodItemFactory extends CustomItemFactory{
	protected final Consumer<FoodProperties.Builder> foodProperty;

	public FoodItemFactory(int nutrition){
		this(f -> f.nutrition(nutrition));
	}
	public FoodItemFactory(int nutrition, float saturationMod){
		this(f -> f.nutrition(nutrition).saturationMod(saturationMod));
	}
	public FoodItemFactory(@NotNull Consumer<FoodProperties.Builder> foodProperty){
		this(foodProperty, null, null);
	}
	public FoodItemFactory(@NotNull Consumer<FoodProperties.Builder> foodProperty,
	                       @Nullable Function<Item.Properties, ? extends Item> item){
		this(foodProperty, null, item);
	}
	public FoodItemFactory(@NotNull Consumer<FoodProperties.Builder> foodProperty,
	                       @Nullable Supplier<Item.Properties> property,
	                       @Nullable Function<Item.Properties, ? extends Item> item){
		super(property, item);
		this.foodProperty = Objects.requireNonNull(foodProperty, "foodProperty == null");
	}

	@Override protected Item.Properties getProperties(){
		FoodProperties.Builder foodPropertyBuilder = new FoodProperties.Builder();
		this.foodProperty.accept(foodPropertyBuilder);
		return super.getProperties().food(foodPropertyBuilder.build());
	}
}
