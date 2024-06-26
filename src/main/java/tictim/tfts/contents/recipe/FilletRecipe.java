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

public abstract class FilletRecipe extends TFTSCustomRecipe<FilletRecipe.Context, FilletRecipe.ResultProcessor, ItemStack>
		implements SimplePreviewRecipe{
	public static final SimpleRecipeType<FilletRecipe> TYPE = new SimpleRecipeType<>(TFTSMod.id("fillet"));

	public FilletRecipe(@NotNull ResourceLocation id){
		super(id);
	}

	@Override @NotNull public final RecipeType<?> getType(){
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
		void addResultExperience(float amount);
	}
}
