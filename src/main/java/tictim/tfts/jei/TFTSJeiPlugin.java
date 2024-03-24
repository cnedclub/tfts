package tictim.tfts.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.helpers.IJeiHelpers;
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
import tictim.tfts.contents.TFTSRecipes;
import tictim.tfts.contents.inventory.FilletTableMenu;
import tictim.tfts.contents.item.Thing;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
@JeiPlugin
public class TFTSJeiPlugin implements IModPlugin{

	@Override public ResourceLocation getPluginUid(){
		return TFTSMod.id("jei_plugin");
	}

	@Override public void registerCategories(IRecipeCategoryRegistration registration){
		IJeiHelpers jeiHelpers = registration.getJeiHelpers();
		IGuiHelper guiHelper = jeiHelpers.getGuiHelper();
		registration.addRecipeCategories(
				new FilletRecipeCategory(guiHelper)
		);
	}

	@Override public void registerRecipes(IRecipeRegistration registration){
		Minecraft minecraft = Minecraft.getInstance();
		ClientLevel world = minecraft.level;
		if(world==null) return;
		RecipeManager recipeManager = world.getRecipeManager();

		registration.addRecipes(FilletRecipeCategory.RECIPE_TYPE, new ArrayList<>(recipeManager.getAllRecipesFor(TFTSRecipes.FILLET.get())));
	}

	@Override public void registerRecipeCatalysts(IRecipeCatalystRegistration registration){
		registration.addRecipeCatalyst(new ItemStack(Thing.FILLET_TABLE.asItem()), FilletRecipeCategory.RECIPE_TYPE);
	}

	@Override public void registerGuiHandlers(IGuiHandlerRegistration registration){
		registration.addRecipeClickArea(FilletTableScreen.class, 1, 1, 16, 16, FilletRecipeCategory.RECIPE_TYPE);
	}

	@Override public void registerRecipeTransferHandlers(IRecipeTransferRegistration registration){
		registration.addRecipeTransferHandler(FilletTableMenu.class, TFTSMenus.FILLET_TABLE.get(), FilletRecipeCategory.RECIPE_TYPE, 0, 1, 1, 36);
	}
}
