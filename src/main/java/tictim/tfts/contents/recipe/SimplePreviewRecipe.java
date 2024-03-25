package tictim.tfts.contents.recipe;

import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;

public interface SimplePreviewRecipe{
	@Nullable Ingredient previewIngredient();
	@Nullable @Unmodifiable List<ChancedOutput> previewResults();
}
