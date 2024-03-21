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
import tictim.tfts.contents.block.FishPreparationTableBlock;
import tictim.tfts.contents.block.FishPreparationTableBlockEntity;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING;

public class FishPreparationContext implements FishPreparationRecipe.Context{
	private final IItemHandlerModifiable fishPreparationTable;
	private final MinecraftServer server;
	@Nullable private final Player player;
	@Nullable private final FishPreparationTableBlockEntity blockEntity;

	private boolean worldPositionCached;
	@Nullable private Vec3 worldPositionCache;

	public FishPreparationContext(@NotNull IItemHandlerModifiable fishPreparationTable,
	                              @NotNull MinecraftServer server,
	                              @Nullable Player player,
	                              @Nullable FishPreparationTableBlockEntity blockEntity){
		this.player = player;
		this.server = server;
		this.fishPreparationTable = fishPreparationTable;
		this.blockEntity = blockEntity;
	}

	@Override @NotNull public ItemStack fish(){
		return fishPreparationTable.getStackInSlot(0);
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
			worldPositionCache = blockEntity!=null ? FishPreparationTableBlock.getTableCenter(
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
