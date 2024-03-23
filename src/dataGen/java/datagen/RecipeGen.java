package datagen;

import datagen.recipe.FishPreparationRecipeBuilder;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.common.Tags;
import org.jetbrains.annotations.NotNull;
import tictim.tfts.contents.item.Fish;
import tictim.tfts.contents.item.Thing;

import java.util.function.Consumer;

import static tictim.tfts.TFTSMod.id;

public class RecipeGen extends RecipeProvider{
	public RecipeGen(PackOutput output){
		super(output);
	}

	@Override protected void buildRecipes(@NotNull Consumer<FinishedRecipe> writer){
		ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, Thing.COOKING_MORTAR)
				.pattern("1")
				.pattern("2")
				.define('1', Tags.Items.RODS_WOODEN)
				.define('2', Items.BOWL)
				.unlockedBy(getHasName(Items.STICK), has(Items.STICK))
				.save(writer);

		ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, Thing.FISH_PREPARATION_TABLE)
				.pattern("111")
				.pattern("2 2")
				.define('1', Items.SMOOTH_STONE_SLAB)
				.define('2', ItemTags.PLANKS)
				.unlockedBy("has_fish", has(ItemTags.FISHES))
				.save(writer);

		/*
		  Fish fillets

		  Based on range of fish size
		  Avg under/around 50cm: Small fish fillet 1
		  Avg over 50cm: Fish fillet 1

		  could be adjusted by size of fish

		  Give additional bone meals based on max size
		  max under 50cm: 1
		  under 80cm: 2
		  under 100cm: 3
		  over: 4

		  Super big: Fish fillet 2

		 */
		//common fish

		fishPreparation().fish(Fish.CATFISH)
				.out(Thing.FISH_FILLET, 2)
				.chanced(Items.BONE_MEAL, 4, .8)
				.finish(writer, fishPreparationId("catfish"));

		fishPreparation().fish(Fish.BROWN_CROAKER)
				.out(Thing.SMALL_FISH_FILLET, 2)
				.chanced(Items.BONE_MEAL, 2, .8)
				.finish(writer, fishPreparationId("brown_croaker"));

		fishPreparation().fish(Fish.SHRIMP)
				.out(Thing.SMALL_FISH_FILLET, 1)
				.finish(writer, fishPreparationId("shrimp"));

		fishPreparation().fish(Fish.ROCKFISH)
				.out(Thing.SMALL_FISH_FILLET, 2)
				.chanced(Items.BONE_MEAL, 3, .8)
				.chanced(Items.COBBLESTONE, 2, .1)
				.finish(writer, fishPreparationId("rockfish"));

		fishPreparation().fish(Fish.CARP)
				.out(Thing.SMALL_FISH_FILLET, 1)
				.chanced(Items.BONE_MEAL, 2, .8)
				.finish(writer, fishPreparationId("carp"));
		//Creeperfish, Barreleye TODO
		fishPreparation().fish(Fish.CREEPER_FISH)
				.out(Items.ROTTEN_FLESH)
				.out(Items.GUNPOWDER)
				.chanced(Items.GUNPOWDER, .5)
				.finish(writer, fishPreparationId("creeper_fish"));

		fishPreparation().fish(Fish.BARRELEYE)
				.out(Thing.SMALL_FISH_FILLET, 1)
				.chanced(Thing.BARRELEYE_EYE, .4)
				.finish(writer, fishPreparationId("barreleye"));

		//uncommon fish

		fishPreparation().fish(Fish.GARIBALDI_DAMSELFISH)
				.out(Thing.SMALL_FISH_FILLET)
				.chanced(Items.BONE_MEAL, .8)
				.finish(writer, fishPreparationId("garibaldi_damselfish"));

		fishPreparation().fish(Fish.FLYING_FISH)
				.out(Thing.SMALL_FISH_FILLET)
				.chanced(Items.BONE_MEAL, .8)
				.finish(writer, fishPreparationId("flying_fish"));

		fishPreparation().fish(Fish.TUNA)
				.out(Thing.FISH_FILLET)
				.chanced(Items.BONE_MEAL, 4, .8)
				.finish(writer, fishPreparationId("tuna"));

		//Rare fish

		fishPreparation().fish(Fish.OARFISH)
				.out(Thing.FISH_FILLET, 2)
				.chanced(Items.BONE_MEAL, 4, .8)
				.finish(writer, fishPreparationId("oarfish"));

		fishPreparation().fish(Fish.MELIBE)
				.out(Thing.SMALL_FISH_FILLET)
				.chanced(Items.BONE_MEAL, .8)
				.chanced(Items.SLIME_BALL, .4)
				.finish(writer, fishPreparationId("melibe"));

		fishPreparation().fish(Fish.MARLIN)
				.out(Thing.FISH_FILLET, 2)
				.chanced(Items.BONE_MEAL, 4, .8)
				.finish(writer, fishPreparationId("marlin"));

		fishPreparation().fish(Fish.PENGUIN)
				.out(Thing.FISH_FILLET, 1)
				.chanced(Items.BONE_MEAL, 3, .8)
				.finish(writer, fishPreparationId("penguin"));

		//SR

		fishPreparation().fish(Fish.OCEAN_SUNFISH)
				.out(Thing.FISH_FILLET, 2)
				.chanced(Items.BONE_MEAL, 4, .8)
				.finish(writer, fishPreparationId("ocean_sunfish"));

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
