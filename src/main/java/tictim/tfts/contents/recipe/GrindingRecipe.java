package tictim.tfts.contents.recipe;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tictim.tfts.TFTSMod;

public abstract class GrindingRecipe extends TFTSCustomRecipe<GrindingRecipe.Context, GrindingRecipe.ResultProcessor, ItemStack>
		implements SimplePreviewRecipe{
	public static final SimpleRecipeType<GrindingRecipe> TYPE = new SimpleRecipeType<>(TFTSMod.id("grinding"));

	public GrindingRecipe(@NotNull ResourceLocation id){
		super(id);
	}

	@Override @NotNull public RecipeType<?> getType(){
		return TYPE;
	}

	public interface Context{
		@NotNull ItemStack input();
		@NotNull MinecraftServer server();
		@Nullable ServerPlayer player();
		@Nullable Vec3 worldPosition();

		@Nullable ServerLevel level();
	}

	public interface ResultProcessor{
		@NotNull RandomSource random();

		void addResultItem(@NotNull ItemStack stack);
	}
}
