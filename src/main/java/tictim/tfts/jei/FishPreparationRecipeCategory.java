package tictim.tfts.jei;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.ChatFormatting;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import tictim.tfts.TFTSMod;
import tictim.tfts.client.Textures;
import tictim.tfts.contents.item.Thing;
import tictim.tfts.contents.recipe.ChancedOutput;
import tictim.tfts.contents.recipe.FishPreparationRecipe;
import tictim.tfts.contents.recipe.SimpleFishPreparationRecipe;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class FishPreparationRecipeCategory implements IRecipeCategory<FishPreparationRecipe>{
	public static final RecipeType<FishPreparationRecipe> RECIPE_TYPE = new RecipeType<>(TFTSMod.id("fish_preparation"), FishPreparationRecipe.class);

	private final IDrawable background;
	private final IDrawable icon;

	public FishPreparationRecipeCategory(IGuiHelper helper){
		background = helper.createDrawable(Textures.INVENTORY, 0, 0, 176, 90);
		icon = helper.createDrawableItemStack(new ItemStack(Thing.FISH_PREPARATION_TABLE.asItem()));
	}

	@Override public RecipeType<FishPreparationRecipe> getRecipeType(){
		return RECIPE_TYPE;
	}
	@Override public Component getTitle(){
		return Component.translatable("jei.tfts.fish_preparation");
	}
	@Override public IDrawable getBackground(){
		return background;
	}

	@Override public IDrawable getIcon(){
		return icon;
	}

	@Override public void setRecipe(IRecipeLayoutBuilder builder, FishPreparationRecipe recipe, IFocusGroup focuses){
		if(recipe instanceof SimpleFishPreparationRecipe sRecipe){
			builder.addSlot(RecipeIngredientRole.INPUT, 1, 9)
					.addIngredients(sRecipe.getFish());
			int i = 0;
			for(ChancedOutput result : sRecipe.getResults()){
				ItemStack stack = result.stack();
				double chance = result.chance();
				builder.addSlot(RecipeIngredientRole.OUTPUT, 61+18*i, 9)
						.addItemStack(stack).addTooltipCallback(((recipeSlotView, tooltip) -> {
							if(chance==1.0) return;
							tooltip.add(Component.translatable("jei.tfts.tooltip.chanced", chance*100).withStyle(ChatFormatting.GRAY));
						}));
				i++;
			}
		}

	}
}
