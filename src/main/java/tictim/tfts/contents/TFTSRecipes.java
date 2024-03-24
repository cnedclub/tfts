package tictim.tfts.contents;

import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.registries.RegistryObject;
import tictim.tfts.contents.recipe.FilletRecipe;
import tictim.tfts.contents.recipe.SimpleFilletRecipe;

import static tictim.tfts.TFTSMod.id;
import static tictim.tfts.contents.TFTSRegistries.RECIPES;
import static tictim.tfts.contents.TFTSRegistries.RECIPE_TYPES;

public final class TFTSRecipes{
	private TFTSRecipes(){}
	public static void init(){}

	public static final RegistryObject<RecipeType<FilletRecipe>> FILLET = RECIPE_TYPES.register("fillet",
			() -> RecipeType.simple(id("fillet")));

	public static final RegistryObject<RecipeSerializer<SimpleFilletRecipe>> SIMPLE_FILLETING = RECIPES.register("fillet",
			() -> SimpleFilletRecipe.SERIALIZER);
}
