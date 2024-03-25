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
import net.minecraft.world.item.crafting.Ingredient;
import tictim.tfts.contents.recipe.ChancedOutput;
import tictim.tfts.contents.recipe.SimplePreviewRecipe;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class SimplePreviewRecipeCategory<R extends SimplePreviewRecipe> implements IRecipeCategory<R>{
	private final RecipeType<R> recipeType;
	private final IDrawable background;
	private final IDrawable icon;

	public SimplePreviewRecipeCategory(IGuiHelper helper, RecipeType<R> recipeType, IDrawable icon){
		this.recipeType = recipeType;
		this.background = helper.createBlankDrawable(176, 18);
		this.icon = icon;
	}

	@Override public RecipeType<R> getRecipeType(){
		return this.recipeType;
	}
	@Override public Component getTitle(){
		return Component.translatable("jei.tfts.fillet");
	}
	@Override public IDrawable getBackground(){
		return background;
	}

	@Override public IDrawable getIcon(){
		return icon;
	}

	@Override public void setRecipe(IRecipeLayoutBuilder builder, R recipe, IFocusGroup focuses){
		Ingredient ingredient = recipe.previewIngredient();
		List<ChancedOutput> results = recipe.previewResults();
		if(ingredient==null||results==null) return;

		builder.addSlot(RecipeIngredientRole.INPUT, 1, 0)
				.addIngredients(ingredient);
		int i = 0;
		for(ChancedOutput result : results){
			ItemStack stack = result.stack();
			double chance = result.chance();
			builder.addSlot(RecipeIngredientRole.OUTPUT, 61+18*i, 0)
					.addItemStack(stack).addTooltipCallback(((recipeSlotView, tooltip) -> {
						if(chance==1.0) return;
						tooltip.add(Component.translatable("jei.tfts.tooltip.chanced", chance*100).withStyle(ChatFormatting.GRAY));
					}));
			i++;
		}
	}
}
