package datagen.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tictim.tfts.contents.recipe.ChancedOutput;
import tictim.tfts.contents.recipe.SimpleFilletRecipe;

import java.util.List;
import java.util.Objects;

public class FinishedFilletRecipe implements FinishedRecipe{
	protected final ResourceLocation id;
	protected final Ingredient input;
	protected final List<ChancedOutput> results;
	protected final float experience;

	public FinishedFilletRecipe(@NotNull ResourceLocation id,
	                            @NotNull Ingredient input,
	                            @NotNull List<ChancedOutput> results, float experience){
		this.id = Objects.requireNonNull(id);
		this.input = Objects.requireNonNull(input);
		this.results = Objects.requireNonNull(results);
		this.experience = experience;
	}

	@Override public void serializeRecipeData(@NotNull JsonObject json){
		json.add("ingredient", this.input.toJson());
		if(this.results.size()==1){
			json.add("result", this.results.get(0).toJson());
		}else{
			JsonArray a = new JsonArray();
			for(ChancedOutput o : this.results) a.add(o.toJson());
			json.add("result", a);
		}
		if(this.experience>0) json.addProperty("experience", this.experience);
	}

	@Override @NotNull public ResourceLocation getId(){
		return this.id;
	}
	@Override @NotNull public RecipeSerializer<?> getType(){
		return SimpleFilletRecipe.SERIALIZER;
	}

	@Override @Nullable public JsonObject serializeAdvancement(){
		return null;
	}
	@Override @Nullable public ResourceLocation getAdvancementId(){
		return null;
	}
}
