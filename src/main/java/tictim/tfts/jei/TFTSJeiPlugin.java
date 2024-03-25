package tictim.tfts.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.*;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;
import tictim.tfts.TFTSMod;
import tictim.tfts.client.screen.FilletTableScreen;
import tictim.tfts.contents.TFTSMenus;
import tictim.tfts.contents.inventory.FilletTableMenu;
import tictim.tfts.contents.item.Thing;
import tictim.tfts.contents.recipe.FilletRecipe;
import tictim.tfts.contents.recipe.GrindingRecipe;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
@JeiPlugin
public class TFTSJeiPlugin implements IModPlugin{
	public static final RecipeType<FilletRecipe> FILLET_RECIPE_TYPE = new RecipeType<>(FilletRecipe.TYPE.id(), FilletRecipe.class);
	public static final RecipeType<GrindingRecipe> GRINDING_RECIPE_TYPE = new RecipeType<>(GrindingRecipe.TYPE.id(), GrindingRecipe.class);

	@Override public ResourceLocation getPluginUid(){
		return TFTSMod.id("jei_plugin");
	}

	@Override public void registerCategories(IRecipeCategoryRegistration registration){
		IJeiHelpers jeiHelpers = registration.getJeiHelpers();
		IGuiHelper helper = jeiHelpers.getGuiHelper();
		registration.addRecipeCategories(
				new SimplePreviewRecipeCategory<>(helper, FILLET_RECIPE_TYPE, helper.createDrawableItemStack(new ItemStack(Thing.FILLET_TABLE))),
				new SimplePreviewRecipeCategory<>(helper, GRINDING_RECIPE_TYPE, helper.createDrawableItemStack(new ItemStack(Thing.COOKING_MORTAR)))
		);
	}

	@Override public void registerRecipes(IRecipeRegistration registration){
		Minecraft minecraft = Minecraft.getInstance();
		ClientLevel world = minecraft.level;
		if(world==null) return;
		RecipeManager recipeManager = world.getRecipeManager();

		registration.addRecipes(FILLET_RECIPE_TYPE, recipeManager.getAllRecipesFor(FilletRecipe.TYPE));
		registration.addRecipes(GRINDING_RECIPE_TYPE, recipeManager.getAllRecipesFor(GrindingRecipe.TYPE));
	}

	@Override public void registerRecipeCatalysts(IRecipeCatalystRegistration registration){
		registration.addRecipeCatalyst(new ItemStack(Thing.FILLET_TABLE), FILLET_RECIPE_TYPE);
		registration.addRecipeCatalyst(new ItemStack(Thing.COOKING_MORTAR), GRINDING_RECIPE_TYPE);
	}

	@Override public void registerGuiHandlers(IGuiHandlerRegistration registration){
		registration.addRecipeClickArea(FilletTableScreen.class, 1, 1, 16, 16, FILLET_RECIPE_TYPE);
	}

	@Override public void registerRecipeTransferHandlers(IRecipeTransferRegistration registration){
		registration.addRecipeTransferHandler(FilletTableMenu.class, TFTSMenus.FILLET_TABLE.get(), FILLET_RECIPE_TYPE, 0, 1, 1, 36);
	}
}
