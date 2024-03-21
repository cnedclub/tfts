package tictim.tfts.contents;

import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.registries.RegistryObject;
import tictim.tfts.contents.recipe.FishPreparationRecipe;
import tictim.tfts.contents.recipe.SimpleFishPreparationRecipe;

import static tictim.tfts.TFTSMod.id;
import static tictim.tfts.contents.TFTSRegistries.RECIPES;
import static tictim.tfts.contents.TFTSRegistries.RECIPE_TYPES;

public final class TFTSRecipes{
	private TFTSRecipes(){}
	public static void init(){}

	public static final RegistryObject<RecipeType<FishPreparationRecipe>> PREPARATION = RECIPE_TYPES.register("preparation",
			() -> RecipeType.simple(id("preparation")));

	public static final RegistryObject<RecipeSerializer<SimpleFishPreparationRecipe>> SIMPLE_PREPARATION = RECIPES.register("preparation",
			() -> SimpleFishPreparationRecipe.SERIALIZER);
}
