package tictim.tfts.contents.recipe;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class GrindingContext implements GrindingRecipe.Context{
	private final ServerPlayer player;
	private final InteractionHand inputItemHand;

	public GrindingContext(@NotNull ServerPlayer player, @NotNull InteractionHand inputItemHand){
		this.player = player;
		this.inputItemHand = inputItemHand;
	}

	@Override @NotNull public ItemStack input(){
		return this.player.getItemInHand(this.inputItemHand);
	}
	@Override @NotNull public MinecraftServer server(){
		return this.player.server;
	}
	@Override @NotNull public ServerPlayer player(){
		return this.player;
	}
	@Override @NotNull public Vec3 worldPosition(){
		return this.player.position();
	}
	@Override @NotNull public ServerLevel level(){
		return this.player.serverLevel();
	}
}
