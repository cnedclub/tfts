package tictim.tfts.contents.recipe;

import com.google.gson.JsonObject;
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
import tictim.tfts.utils.A;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SimpleFilletRecipe extends FilletRecipe{
	public static final RecipeSerializer<SimpleFilletRecipe> SERIALIZER = new Serializer();

	private final Ingredient input;
	private final List<ChancedOutput> results;
	private final float experience;

	public SimpleFilletRecipe(@NotNull ResourceLocation id,
	                          @NotNull Ingredient input,
	                          @NotNull List<ChancedOutput> results,
	                          float experience){
		super(id);
		this.input = Objects.requireNonNull(input, "fish == null");
		this.results = Objects.requireNonNull(results, "results == null");
		this.experience = Float.isNaN(experience) ? 0 : Math.max(0, experience);
	}

	@Override @NotNull public RecipeSerializer<?> getSerializer(){
		return SERIALIZER;
	}

	@Override @Nullable public RecipeResult<Context, ResultProcessor, ItemStack> matches(@NotNull Context context){
		return this.input.test(context.input()) ? this::process : null;
	}

	@NotNull private ItemStack process(Context context, ResultProcessor processor){
		ItemStack input = context.input();
		int count = input.getCount();
		for(ChancedOutput o : this.results){
			ItemStack stack = o.copyNAmount(count, processor.random());
			if(!stack.isEmpty()) processor.addResultItem(stack);
		}
		if(this.experience>0) processor.addResultExperience(this.experience*count);
		return ItemStack.EMPTY;
	}

	@Override @NotNull public Ingredient previewIngredient(){
		return this.input;
	}
	@Override @NotNull @Unmodifiable public List<ChancedOutput> previewResults(){
		return this.results;
	}

	public static class Serializer implements RecipeSerializer<SimpleFilletRecipe>{
		@Override @NotNull public SimpleFilletRecipe fromJson(@NotNull ResourceLocation id, @NotNull JsonObject json){
			Ingredient input = CraftingHelper.getIngredient(GsonHelper.getNonNull(json, "ingredient"), false);
			List<ChancedOutput> results = A.parseObjectOrArray(GsonHelper.getNonNull(json, "result"),
					ChancedOutput::fromJson, "result");
			float experience = GsonHelper.getAsFloat(json, "experience", 0);
			return new SimpleFilletRecipe(id, input, results, experience);
		}

		@Override @Nullable public SimpleFilletRecipe fromNetwork(@NotNull ResourceLocation id, @NotNull FriendlyByteBuf buf){
			Ingredient input = Ingredient.fromNetwork(buf);
			List<ChancedOutput> results = new ArrayList<>();
			for(int i = buf.readVarInt(); i>0; i--){
				results.add(ChancedOutput.fromNetwork(buf));
			}
			float experience = buf.readFloat();
			return new SimpleFilletRecipe(id, input, results, experience);
		}

		@Override public void toNetwork(@NotNull FriendlyByteBuf buf, @NotNull SimpleFilletRecipe recipe){
			recipe.input.toNetwork(buf);
			buf.writeVarInt(recipe.results.size());
			for(ChancedOutput o : recipe.results){
				o.toNetwork(buf);
			}
			buf.writeFloat(recipe.experience);
		}
	}
}
