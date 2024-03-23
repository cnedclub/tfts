package datagen;

import datagen.recipe.FishPreparationRecipeBuilder;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.common.Tags;
import org.jetbrains.annotations.NotNull;
import tictim.tfts.contents.item.Thing;

import java.util.function.Consumer;

import static tictim.tfts.TFTSMod.id;

public class RecipeGen extends RecipeProvider{
	public RecipeGen(PackOutput output){
		super(output);
	}

	@Override protected void buildRecipes(@NotNull Consumer<FinishedRecipe> writer){
		fishPreparation().fish(Tags.Items.COBBLESTONE)
				.out(Items.DIAMOND, 64)
				.finish(writer, fishPreparationId("cobblestone"));

		fishPreparation().fish(Items.ACACIA_SLAB)
				.out(Items.GOLD_BLOCK, 3)
				.finish(writer, fishPreparationId("acacia_slab"));

		fishPreparation().fish(Items.BEE_SPAWN_EGG)
				.chanced(Items.ITEM_FRAME, 0.5)
				.finish(writer, fishPreparationId("bee_spawn_egg"));

		cooking(writer, Thing.SMALL_FISH_FILLET, Thing.COOKED_SMALL_FISH_FILLET);
		cooking(writer, Thing.FISH_FILLET, Thing.COOKED_FISH_FILLET);
	}

	private static void cooking(@NotNull Consumer<FinishedRecipe> writer, ItemLike input, ItemLike output){
		SimpleCookingRecipeBuilder.smelting(Ingredient.of(input), RecipeCategory.FOOD, output, 0.35f, 200)
				.unlockedBy(getHasName(input), has(input))
				.save(writer);
		simpleCookingRecipe(writer, "smoking", RecipeSerializer.SMOKING_RECIPE, 100,
				input, output, 0.35f);
		simpleCookingRecipe(writer, "campfire_cooking", RecipeSerializer.CAMPFIRE_COOKING_RECIPE, 600,
				input, output, 0.35f);
	}

	private static FishPreparationRecipeBuilder fishPreparation(){
		return new FishPreparationRecipeBuilder();
	}

	private static ResourceLocation fishPreparationId(String path){
		return id("fish_preparation/"+path);
	}
}
