package tictim.tfts.contents.recipe;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.common.crafting.CraftingHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SimpleFishPreparationRecipe extends FishPreparationRecipe{
	public static final RecipeSerializer<SimpleFishPreparationRecipe> SERIALIZER = new Serializer();

	private final Ingredient fish;
	private final List<ChancedOutput> results;
	private final float experience;

	public SimpleFishPreparationRecipe(@NotNull ResourceLocation id,
	                                   @NotNull Ingredient fish,
	                                   @NotNull List<ChancedOutput> results,
	                                   float experience){
		super(id);
		this.fish = Objects.requireNonNull(fish, "fish == null");
		this.results = Objects.requireNonNull(results, "results == null");
		this.experience = Float.isNaN(experience) ? 0 : Math.max(0, experience);
	}

	@Override @NotNull public RecipeSerializer<?> getSerializer(){
		return SERIALIZER;
	}

	@Override @Nullable public Result matches(@NotNull Context context){
		return this.fish.test(context.fish()) ? this::process : null;
	}

	@NotNull private ItemStack process(Context context, ResultProcessor processor){
		ItemStack fish = context.fish();
		int count = fish.getCount();
		for(ChancedOutput o : this.results){
			ItemStack stack = o.copyNAmount(count, processor.random());
			if(!stack.isEmpty()) processor.addResultItem(stack);
		}
		if(this.experience>0) processor.addResultExperience(this.experience*count);
		return ItemStack.EMPTY;
	}

	@Override @NotNull @Unmodifiable public List<ChancedOutput> previewResults(@NotNull RegistryAccess registryAccess){
		return this.results;
	}

	public static class Serializer implements RecipeSerializer<SimpleFishPreparationRecipe>{
		@Override @NotNull public SimpleFishPreparationRecipe fromJson(@NotNull ResourceLocation id, @NotNull JsonObject json){
			Ingredient fish = CraftingHelper.getIngredient(GsonHelper.getNonNull(json, "ingredient"), false);
			List<ChancedOutput> results = new ArrayList<>();
			JsonElement result = GsonHelper.getNonNull(json, "result");
			if(result.isJsonArray()){
				for(JsonElement e : result.getAsJsonArray()){
					if(!e.isJsonObject()){
						throw new JsonParseException("Expected elements of field 'result' to be an object");
					}
					results.add(ChancedOutput.fromJson(e.getAsJsonObject()));
				}
			}else if(result.isJsonObject()){
				results.add(ChancedOutput.fromJson(result.getAsJsonObject()));
			}else{
				throw new JsonParseException("Expected field 'result' to be either an array or an object");
			}
			float experience = GsonHelper.getAsFloat(json, "experience", 0);
			return new SimpleFishPreparationRecipe(id, fish, results, experience);
		}

		@Override @Nullable public SimpleFishPreparationRecipe fromNetwork(@NotNull ResourceLocation id, @NotNull FriendlyByteBuf buf){
			Ingredient fish = Ingredient.fromNetwork(buf);
			List<ChancedOutput> results = new ArrayList<>();
			for(int i = buf.readVarInt(); i>0; i--){
				results.add(ChancedOutput.fromNetwork(buf));
			}
			float experience = buf.readFloat();
			return new SimpleFishPreparationRecipe(id, fish, results, experience);
		}

		@Override public void toNetwork(@NotNull FriendlyByteBuf buf, @NotNull SimpleFishPreparationRecipe recipe){
			recipe.fish.toNetwork(buf);
			buf.writeVarInt(recipe.results.size());
			for(ChancedOutput o : recipe.results){
				o.toNetwork(buf);
			}
			buf.writeFloat(recipe.experience);
		}
	}
}
