package tictim.tfts.contents.recipe;

import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public abstract class TFTSCustomRecipe<Context, ResultProcessor, Result> implements Recipe<NoContainer>{
	private final ResourceLocation id;

	public TFTSCustomRecipe(@NotNull ResourceLocation id){
		this.id = Objects.requireNonNull(id, "id == null");
	}

	@Override @NotNull public final ResourceLocation getId(){
		return this.id;
	}

	/**
	 * @param context Context
	 * @return Result action, or {@code null} if the match failed
	 */
	@Nullable public abstract RecipeResult<Context, ResultProcessor, Result> matches(@NotNull Context context);

	/**
	 * @deprecated Not used in logic
	 */
	@Deprecated @Override public final boolean canCraftInDimensions(int width, int height){
		return true;
	}
	/**
	 * @deprecated Not used in logic
	 */
	@Deprecated @Override public final boolean matches(@NotNull NoContainer container, @NotNull Level level){
		return false;
	}
	/**
	 * @deprecated Not used in logic
	 */
	@Deprecated @Override @NotNull public final ItemStack assemble(@NotNull NoContainer container, @NotNull RegistryAccess registryAccess){
		return ItemStack.EMPTY;
	}
	/**
	 * @deprecated Not used in logic
	 */
	@Deprecated @Override @NotNull public final NonNullList<ItemStack> getRemainingItems(@NotNull NoContainer container){
		return NonNullList.create();
	}
	/**
	 * @deprecated Not used in logic
	 */
	@Deprecated @Override @NotNull public final ItemStack getResultItem(@NotNull RegistryAccess registryAccess){
		return ItemStack.EMPTY;
	}
}
