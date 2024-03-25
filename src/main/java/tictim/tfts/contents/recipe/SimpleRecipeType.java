package tictim.tfts.contents.recipe;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import org.jetbrains.annotations.NotNull;

public record SimpleRecipeType<T extends Recipe<?>>(
		@NotNull ResourceLocation id
) implements RecipeType<T>{
	@Override public String toString(){
		return id.toString();
	}
}
