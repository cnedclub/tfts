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

public class SimpleGrindingRecipe extends GrindingRecipe{
	public static final RecipeSerializer<SimpleGrindingRecipe> SERIALIZER = new Serializer();

	private final Ingredient input;
	private final List<ChancedOutput> results;

	public SimpleGrindingRecipe(@NotNull ResourceLocation id,
	                            @NotNull Ingredient input,
	                            @NotNull List<ChancedOutput> results){
		super(id);
		this.input = input;
		this.results = results;
	}

	@Override @NotNull public RecipeSerializer<?> getSerializer(){
		return SERIALIZER;
	}

	@Override public @Nullable RecipeResult<Context, ResultProcessor, ItemStack> matches(@NotNull Context context){
		return this.input.test(context.input()) ? this::process : null;
	}

	private ItemStack process(Context context, ResultProcessor processor){
		ItemStack input = context.input();
		for(ChancedOutput o : this.results){
			ItemStack stack = o.copy(processor.random());
			if(!stack.isEmpty()) processor.addResultItem(stack);
		}
		input.shrink(1);
		return input;
	}

	@Override @NotNull public Ingredient previewIngredient(){
		return this.input;
	}
	@Override @NotNull @Unmodifiable public List<ChancedOutput> previewResults(){
		return this.results;
	}

	public static class Serializer implements RecipeSerializer<SimpleGrindingRecipe>{
		@Override @NotNull public SimpleGrindingRecipe fromJson(@NotNull ResourceLocation id, @NotNull JsonObject json){
			Ingredient input = CraftingHelper.getIngredient(GsonHelper.getNonNull(json, "ingredient"), false);
			List<ChancedOutput> results = A.parseObjectOrArray(GsonHelper.getNonNull(json, "result"),
					ChancedOutput::fromJson, "result");
			return new SimpleGrindingRecipe(id, input, results);
		}

		@Override @Nullable public SimpleGrindingRecipe fromNetwork(@NotNull ResourceLocation id, @NotNull FriendlyByteBuf buf){
			Ingredient input = Ingredient.fromNetwork(buf);
			List<ChancedOutput> results = new ArrayList<>();
			for(int i = buf.readVarInt(); i>0; i--){
				results.add(ChancedOutput.fromNetwork(buf));
			}
			return new SimpleGrindingRecipe(id, input, results);
		}

		@Override public void toNetwork(@NotNull FriendlyByteBuf buf, @NotNull SimpleGrindingRecipe recipe){
			recipe.input.toNetwork(buf);
			buf.writeVarInt(recipe.results.size());
			for(ChancedOutput o : recipe.results){
				o.toNetwork(buf);
			}
		}
	}
}
