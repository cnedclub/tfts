package tictim.tfts.contents.recipe;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.items.IItemHandlerModifiable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tictim.tfts.contents.block.FilletTableBlock;
import tictim.tfts.contents.block.FilletTableBlockEntity;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING;

public class FilletContext implements FilletRecipe.Context{
	private final IItemHandlerModifiable table;
	private final MinecraftServer server;
	@Nullable private final Player player;
	@Nullable private final FilletTableBlockEntity blockEntity;

	private boolean worldPositionCached;
	@Nullable private Vec3 worldPositionCache;

	public FilletContext(@NotNull IItemHandlerModifiable table,
	                     @NotNull MinecraftServer server,
	                     @Nullable Player player,
	                     @Nullable FilletTableBlockEntity blockEntity){
		this.player = player;
		this.server = server;
		this.table = table;
		this.blockEntity = blockEntity;
	}

	@Override @NotNull public ItemStack input(){
		return table.getStackInSlot(0);
	}
	@Override @NotNull public MinecraftServer server(){
		return server;
	}
	@Override @Nullable public ServerPlayer player(){
		return player instanceof ServerPlayer sp ? sp : null;
	}
	@Override @Nullable public Vec3 worldPosition(){
		if(!worldPositionCached){
			worldPositionCached = true;
			worldPositionCache = blockEntity!=null ? FilletTableBlock.getTableCenter(
					blockEntity.getBlockPos(), blockEntity.getBlockState().getValue(HORIZONTAL_FACING)) :
					player!=null ? player.position() : null;
		}
		return worldPositionCache;
	}
	@Override @Nullable public ServerLevel level(){
		ServerPlayer p = player();
		return p!=null ? p.serverLevel() : null;
	}
}
