package datagen.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tictim.tfts.contents.TFTSRecipes;
import tictim.tfts.contents.recipe.ChancedOutput;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public class FilletRecipeBuilder{
	@Nullable private Ingredient fish;
	@Nullable private List<ChancedOutput> results;

	public FilletRecipeBuilder fish(@NotNull TagKey<Item> tag){
		return fish(Ingredient.of(tag));
	}
	public FilletRecipeBuilder fish(@NotNull ItemLike item){
		return fish(Ingredient.of(item));
	}
	public FilletRecipeBuilder fish(@NotNull Ingredient ingredient){
		this.fish = ingredient;
		return this;
	}

	public FilletRecipeBuilder out(@NotNull ItemLike item){
		return out(item, 1);
	}
	public FilletRecipeBuilder out(@NotNull ItemLike item, int count){
		return out(new ChancedOutput(new ItemStack(item, count), 1));
	}
	public FilletRecipeBuilder out(@NotNull ItemStack stack){
		return out(new ChancedOutput(stack, 1));
	}
	public FilletRecipeBuilder chanced(@NotNull ItemLike item, double chance){
		return chanced(item, 1, chance);
	}
	public FilletRecipeBuilder chanced(@NotNull ItemLike item, int count, double chance){
		return out(new ChancedOutput(new ItemStack(item, count), chance));
	}
	public FilletRecipeBuilder chanced(@NotNull ItemStack stack, double chance){
		return out(new ChancedOutput(stack, chance));
	}

	public FilletRecipeBuilder out(@NotNull ChancedOutput out){
		if(this.results==null) this.results = new ArrayList<>();
		this.results.add(Objects.requireNonNull(out, "out == null"));
		return this;
	}

	public FilletRecipeBuilder noResults(){
		this.results = new ArrayList<>();
		return this;
	}

	public void finish(@NotNull Consumer<FinishedRecipe> consumer, @NotNull ResourceLocation id){
		validate(id);
		consumer.accept(createFinishedRecipe(id, Objects.requireNonNull(this.fish), Objects.requireNonNull(this.results)));
	}

	protected void validate(@NotNull ResourceLocation id){
		if(this.fish==null){
			throw new IllegalStateException("Invalid data-generated recipe "+id+": No fish ingredient set");
		}
		if(this.results==null){
			throw new IllegalStateException("Invalid data-generated recipe "+id+": No results set");
		}
	}

	protected FinishedRecipe createFinishedRecipe(@NotNull ResourceLocation id, @NotNull Ingredient fish, @NotNull List<ChancedOutput> results){
		return new Finished(id, fish, results);
	}

	public static class Finished implements FinishedRecipe{
		protected final ResourceLocation id;
		protected final Ingredient fish;
		protected final List<ChancedOutput> results;

		public Finished(@NotNull ResourceLocation id, @NotNull Ingredient fish, @NotNull List<ChancedOutput> results){
			this.id = Objects.requireNonNull(id);
			this.fish = Objects.requireNonNull(fish);
			this.results = Objects.requireNonNull(results);
		}


		@Override public void serializeRecipeData(@NotNull JsonObject json){
			json.add("ingredient", this.fish.toJson());
			if(this.results.size()==1){
				json.add("result", this.results.get(0).toJson());
			}else{
				JsonArray a = new JsonArray();
				for(ChancedOutput o : this.results) a.add(o.toJson());
				json.add("result", a);
			}
		}

		@Override @NotNull public ResourceLocation getId(){
			return this.id;
		}
		@Override @NotNull public RecipeSerializer<?> getType(){
			return TFTSRecipes.SIMPLE_FILLETING.get();
		}

		@Override @Nullable public JsonObject serializeAdvancement(){
			return null;
		}
		@Override @Nullable public ResourceLocation getAdvancementId(){
			return null;
		}
	}
}
