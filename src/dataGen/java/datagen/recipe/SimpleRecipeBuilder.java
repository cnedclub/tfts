package datagen.recipe;

import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tictim.tfts.contents.recipe.ChancedOutput;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Consumer;

public class SimpleRecipeBuilder{
	private final BiFunction<ResourceLocation, SimpleRecipeBuilder, FinishedRecipe> finishedRecipeBuilder;

	@Nullable private Ingredient input;
	@Nullable private List<ChancedOutput> results;
	private float experience;

	public SimpleRecipeBuilder(BiFunction<ResourceLocation, SimpleRecipeBuilder, FinishedRecipe> finishedRecipeBuilder){
		this.finishedRecipeBuilder = finishedRecipeBuilder;
	}

	public Ingredient input(){
		if(this.input==null) throw new IllegalStateException("No input set");
		return input;
	}
	public List<ChancedOutput> results(){
		if(this.results==null) throw new IllegalStateException("No results set");
		return results;
	}
	public float experience(){
		return experience;
	}

	public SimpleRecipeBuilder input(@NotNull TagKey<Item> tag){
		return input(Ingredient.of(tag));
	}
	public SimpleRecipeBuilder input(@NotNull ItemLike item){
		return input(Ingredient.of(item));
	}
	public SimpleRecipeBuilder input(@NotNull Ingredient ingredient){
		this.input = ingredient;
		return this;
	}

	public SimpleRecipeBuilder out(@NotNull ItemLike item){
		return out(item, 1);
	}
	public SimpleRecipeBuilder out(@NotNull ItemLike item, int count){
		return out(new ChancedOutput(new ItemStack(item, count), 1));
	}
	public SimpleRecipeBuilder out(@NotNull ItemStack stack){
		return out(new ChancedOutput(stack, 1));
	}
	public SimpleRecipeBuilder chanced(@NotNull ItemLike item, double chance){
		return chanced(item, 1, chance);
	}
	public SimpleRecipeBuilder chanced(@NotNull ItemLike item, int count, double chance){
		return out(new ChancedOutput(new ItemStack(item, count), chance));
	}
	public SimpleRecipeBuilder chanced(@NotNull ItemStack stack, double chance){
		return out(new ChancedOutput(stack, chance));
	}

	public SimpleRecipeBuilder out(@NotNull ChancedOutput out){
		if(this.results==null) this.results = new ArrayList<>();
		this.results.add(Objects.requireNonNull(out, "out == null"));
		return this;
	}

	public SimpleRecipeBuilder noResults(){
		this.results = new ArrayList<>();
		return this;
	}

	public SimpleRecipeBuilder exp(float exp){
		this.experience = exp;
		return this;
	}

	public void finish(@NotNull Consumer<FinishedRecipe> consumer, @NotNull ResourceLocation id){
		consumer.accept(this.finishedRecipeBuilder.apply(id, this));
	}
}
