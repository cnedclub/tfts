package tictim.tfts.contents.recipe;

import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;
import tictim.tfts.contents.TFTSRecipes;

import java.util.List;
import java.util.Objects;

public abstract class FishPreparationRecipe implements Recipe<NoContainer>{
	private final ResourceLocation id;

	protected FishPreparationRecipe(@NotNull ResourceLocation id){
		this.id = Objects.requireNonNull(id, "id == null");
	}

	@Override @NotNull public final ResourceLocation getId(){
		return this.id;
	}
	@Override @NotNull public final RecipeType<?> getType(){
		return TFTSRecipes.PREPARATION.get();
	}

	/**
	 * @param context Context
	 * @return Result action, or {@code null} if the match failed
	 */
	@Nullable public abstract Result matches(@NotNull Context context);
	@NotNull @Unmodifiable public abstract List<ChancedOutput> previewResults(@NotNull RegistryAccess registryAccess);

	// TODO toast symbol ????????

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

	public interface Context{
		@NotNull ItemStack fish();
		@NotNull MinecraftServer server();
		@Nullable ServerPlayer player();
		@Nullable Vec3 worldPosition();

		@Nullable ServerLevel level();
	}

	public interface ResultProcessor{
		@NotNull RandomSource random();

		void addResultItem(@NotNull ItemStack stack);
		void addResultExperience(float amount);
	}

	@FunctionalInterface
	public interface Result{
		@NotNull ItemStack process(@NotNull Context context, @NotNull ResultProcessor processor);
	}
}
