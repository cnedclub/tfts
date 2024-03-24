package datagen;

import datagen.recipe.FilletRecipeBuilder;
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
import tictim.tfts.contents.item.Bait;
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

		ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, Thing.FILLET_TABLE)
				.pattern(" 12")
				.pattern("333")
				.pattern("4 4")
				.define('1', ItemTags.WOODEN_SLABS)
				.define('2', Items.IRON_SWORD)
				.define('3', Tags.Items.STONE)
				.define('4', ItemTags.PLANKS)
				.unlockedBy("has_fish", has(ItemTags.FISHES))
				.save(writer);

		ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, Thing.TROWEL)
				.pattern("1")
				.pattern("2")
				.define('1', Tags.Items.INGOTS_IRON)
				.define('2', Tags.Items.RODS_WOODEN)
				.unlockedBy(getHasName(Items.IRON_INGOT), has(Items.IRON_INGOT))
				.save(writer);

		ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, Thing.BAIT_BOX)
				.pattern("1")
				.pattern("2")
				.pattern("1")
				.define('1', Tags.Items.STRING)
				.define('2', Items.CHEST)
				.unlockedBy(getHasName(Items.IRON_INGOT), has(Items.IRON_INGOT))
				.save(writer);

		//Baits
		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, Bait.APPLE_BAIT, 3)
				.requires(Items.APPLE)
				.requires(Items.WATER_BUCKET)
				.requires(Thing.STARCH)
				.unlockedBy(getHasName(Thing.STARCH), has(Thing.STARCH))
				.save(writer);

		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, Bait.SWEET_BERRY_BAIT, 3)
				.requires(Items.SWEET_BERRIES)
				.requires(Items.WATER_BUCKET)
				.requires(Thing.STARCH)
				.unlockedBy(getHasName(Thing.STARCH), has(Thing.STARCH))
				.save(writer);

		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, Bait.GLOW_BERRY_BAIT, 3)
				.requires(Items.GLOW_BERRIES)
				.requires(Items.WATER_BUCKET)
				.requires(Thing.STARCH)
				.unlockedBy(getHasName(Thing.STARCH), has(Thing.STARCH))
				.save(writer);

		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, Bait.MELON_BAIT, 3)
				.requires(Items.MELON)
				.requires(Items.WATER_BUCKET)
				.requires(Thing.STARCH)
				.unlockedBy(getHasName(Thing.STARCH), has(Thing.STARCH))
				.save(writer);

		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, Bait.PUMPKIN_BAIT, 3)
				.requires(Items.PUMPKIN)
				.requires(Items.WATER_BUCKET)
				.requires(Thing.STARCH)
				.unlockedBy(getHasName(Thing.STARCH), has(Thing.STARCH))
				.save(writer);

		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, Bait.BEETROOT_BAIT, 3)
				.requires(Items.BEETROOT)
				.requires(Items.WATER_BUCKET)
				.requires(Thing.STARCH)
				.unlockedBy(getHasName(Thing.STARCH), has(Thing.STARCH))
				.save(writer);

		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, Bait.CARROT_BAIT, 3)
				.requires(Items.CARROT)
				.requires(Items.WATER_BUCKET)
				.requires(Thing.STARCH)
				.unlockedBy(getHasName(Thing.STARCH), has(Thing.STARCH))
				.save(writer);

		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, Bait.SEED_BAIT, 3)
				.requires(Tags.Items.SEEDS)
				.requires(Items.WATER_BUCKET)
				.requires(Thing.STARCH)
				.unlockedBy(getHasName(Thing.STARCH), has(Thing.STARCH))
				.save(writer);

		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, Bait.FLOWER_BAIT, 3)
				.requires(ItemTags.FLOWERS)
				.requires(Items.WATER_BUCKET)
				.requires(Thing.STARCH)
				.unlockedBy(getHasName(Thing.STARCH), has(Thing.STARCH))
				.save(writer);

		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, Bait.GOLDEN_APPLE_BAIT, 3)
				.requires(Items.GOLDEN_APPLE)
				.requires(Items.WATER_BUCKET)
				.requires(Thing.STARCH)
				.unlockedBy(getHasName(Thing.STARCH), has(Thing.STARCH))
				.save(writer);

		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, Bait.GOLDEN_CARROT_BAIT, 3)
				.requires(Items.GOLDEN_CARROT)
				.requires(Items.WATER_BUCKET)
				.requires(Thing.STARCH)
				.unlockedBy(getHasName(Thing.STARCH), has(Thing.STARCH))
				.save(writer);

		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, Bait.GLISTERING_MELON_BAIT, 3)
				.requires(Items.GLISTERING_MELON_SLICE)
				.requires(Items.WATER_BUCKET)
				.requires(Thing.STARCH)
				.unlockedBy(getHasName(Thing.STARCH), has(Thing.STARCH))
				.save(writer);

		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, Bait.ENCHANTED_GOLDEN_APPLE_BAIT, 3)
				.requires(Items.ENCHANTED_GOLDEN_APPLE)
				.requires(Items.WATER_BUCKET)
				.requires(Thing.STARCH)
				.unlockedBy(getHasName(Thing.STARCH), has(Thing.STARCH))
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

		//Trashes
		fillet().fish(Fish.BASS)
				.out(Thing.SMALL_FISH_FILLET)
				.finish(writer, filletId("bass"));

		//common fish

		fillet().fish(Fish.CATFISH)
				.out(Thing.FISH_FILLET, 2)
				.chanced(Items.BONE_MEAL, 4, .8)
				.finish(writer, filletId("catfish"));

		fillet().fish(Fish.BROWN_CROAKER)
				.out(Thing.SMALL_FISH_FILLET, 2)
				.chanced(Items.BONE_MEAL, 2, .8)
				.finish(writer, filletId("brown_croaker"));

		fillet().fish(Fish.ROCKFISH)
				.out(Thing.SMALL_FISH_FILLET, 2)
				.chanced(Items.BONE_MEAL, 3, .8)
				.chanced(Items.COBBLESTONE, 2, .1)
				.finish(writer, filletId("rockfish"));

		fillet().fish(Fish.CARP)
				.out(Thing.SMALL_FISH_FILLET, 1)
				.chanced(Items.BONE_MEAL, 2, .8)
				.finish(writer, filletId("carp"));

		fillet().fish(Fish.CREEPER_FISH)
				.chanced(Items.GUNPOWDER, .5)
				.chanced(Items.GUNPOWDER, .5)
				.finish(writer, filletId("creeper_fish"));

		fillet().fish(Fish.BARRELEYE)
				.out(Thing.SMALL_FISH_FILLET, 1)
				.out(Thing.BARRELEYE_EYEBALL, 2)
				.finish(writer, filletId("barreleye"));

		fillet().fish(Fish.ZOMFISH)
				.chanced(Items.ROTTEN_FLESH, .5)
				.chanced(Items.ROTTEN_FLESH, .5)
				.chanced(Items.IRON_INGOT, 0.08)
				.chanced(Items.CARROT, 0.08)
				.chanced(Items.POTATO, 0.08)
				.finish(writer, filletId("zomfish"));

		//uncommon fish

		fillet().fish(Fish.GARIBALDI_DAMSELFISH)
				.out(Thing.SMALL_FISH_FILLET)
				.chanced(Items.BONE_MEAL, .8)
				.finish(writer, filletId("garibaldi_damselfish"));

		fillet().fish(Fish.FLYING_FISH)
				.out(Thing.SMALL_FISH_FILLET)
				.chanced(Items.BONE_MEAL, .8)
				.finish(writer, filletId("flying_fish"));

		fillet().fish(Fish.TUNA)
				.out(Thing.FISH_FILLET)
				.chanced(Items.BONE_MEAL, 4, .8)
				.finish(writer, filletId("tuna"));

		//Rare fish

		fillet().fish(Fish.OARFISH)
				.out(Thing.FISH_FILLET, 2)
				.chanced(Items.BONE_MEAL, 4, .8)
				.finish(writer, filletId("oarfish"));

		fillet().fish(Fish.MELIBE)
				.out(Thing.SMALL_FISH_FILLET)
				.chanced(Items.BONE_MEAL, .8)
				.chanced(Items.SLIME_BALL, .4)
				.finish(writer, filletId("melibe"));

		fillet().fish(Fish.MARLIN)
				.out(Thing.FISH_FILLET, 2)
				.chanced(Items.BONE_MEAL, 4, .8)
				.finish(writer, filletId("marlin"));

		fillet().fish(Fish.PENGUIN)
				.out(Items.CHICKEN, 1)
				.chanced(Items.LEATHER, .5)
				.chanced(Items.BONE_MEAL, 3, .8)
				.finish(writer, filletId("penguin"));

		//SR

		fillet().fish(Fish.OCEAN_SUNFISH)
				.out(Thing.FISH_FILLET, 2)
				.chanced(Items.BONE_MEAL, 4, .8)
				.finish(writer, filletId("ocean_sunfish"));

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

	private static FilletRecipeBuilder fillet(){
		return new FilletRecipeBuilder();
	}

	private static ResourceLocation filletId(String path){
		return id("fillet/"+path);
	}
}
