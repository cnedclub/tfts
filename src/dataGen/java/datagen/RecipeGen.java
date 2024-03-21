package datagen;

import datagen.recipe.FishPreparationRecipeBuilder;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.Tags;
import org.jetbrains.annotations.NotNull;

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
	}

	private static FishPreparationRecipeBuilder fishPreparation(){
		return new FishPreparationRecipeBuilder();
	}

	private static ResourceLocation fishPreparationId(String path){
		return id("fish_preparation/"+path);
	}
}
