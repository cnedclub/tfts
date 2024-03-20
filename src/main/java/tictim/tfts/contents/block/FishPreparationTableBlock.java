package tictim.tfts.contents.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tictim.tfts.utils.A;

import java.util.List;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING;

@SuppressWarnings("deprecation")
public class FishPreparationTableBlock extends Block implements EntityBlock{
	public static final BooleanProperty SOURCE = BooleanProperty.create("source");

	public FishPreparationTableBlock(Properties p){
		super(p);
	}

	@Override protected void createBlockStateDefinition(@NotNull StateDefinition.Builder<Block, BlockState> builder){
		builder.add(SOURCE, HORIZONTAL_FACING);
	}

	@Override @NotNull public InteractionResult use(@NotNull BlockState state, @NotNull Level level,
	                                                @NotNull BlockPos pos, @NotNull Player player,
	                                                @NotNull InteractionHand hand, @NotNull BlockHitResult hit){
		if(!level.isClientSide){
			BlockPos sourceBlockPos = getSourceBlockPos(pos, state);
			if(level.getBlockState(sourceBlockPos)==state.setValue(SOURCE, true)&&
					level.getBlockEntity(sourceBlockPos) instanceof FishPreparationTableBlockEntity be){
				player.openMenu(be);
			}
		}
		return InteractionResult.SUCCESS;
	}

	@Override @Nullable public BlockState getStateForPlacement(BlockPlaceContext ctx){
		Direction facing = ctx.getHorizontalDirection();
		BlockPos clickedPos = ctx.getClickedPos();
		BlockPos thing = getNonSourceBlockPos(clickedPos, facing);
		Level level = ctx.getLevel();
		return level.getBlockState(thing).canBeReplaced(ctx)&&level.getWorldBorder().isWithinBounds(thing) ?
				defaultBlockState().setValue(SOURCE, true).setValue(HORIZONTAL_FACING, facing.getOpposite()) :
				null;
	}

	@Override public void setPlacedBy(@NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState state,
	                                  @Nullable LivingEntity placer, @NotNull ItemStack stack){
		if(!level.isClientSide){
			var thing = getNonSourceBlockPos(pos, state);
			level.setBlock(thing, state.setValue(SOURCE, false), 3);
			level.blockUpdated(pos, Blocks.AIR); // bed block does this for some reason????????
			state.updateNeighbourShapes(level, pos, 3);
		}
		if(stack.hasCustomHoverName()&&level.getBlockEntity(pos) instanceof FishPreparationTableBlockEntity be){
			be.setCustomName(stack.getHoverName());
		}
	}

	@Override @NotNull public List<ItemStack> getDrops(@NotNull BlockState state, @NotNull LootParams.Builder params){
		return state.getValue(SOURCE) ? super.getDrops(state, params) : List.of();
	}

	@Override @NotNull public BlockState updateShape(@NotNull BlockState state, @NotNull Direction facing,
	                                                 @NotNull BlockState facingState, @NotNull LevelAccessor level,
	                                                 @NotNull BlockPos currentPos, @NotNull BlockPos facingPos){
		boolean isSource = state.getValue(SOURCE);
		Direction partDirection = isSource ?
				state.getValue(HORIZONTAL_FACING).getCounterClockWise() :
				state.getValue(HORIZONTAL_FACING).getClockWise();
		if(facing==partDirection){
			return facingState==state.setValue(SOURCE, !isSource) ?
					state : Blocks.AIR.defaultBlockState();
		}
		return super.updateShape(state, facing, facingState, level, currentPos, facingPos);
	}

	@Override public void playerWillDestroy(@NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState state,
	                                        @NotNull Player player){
		if(!level.isClientSide&&player.isCreative()){
			boolean isSource = state.getValue(SOURCE);
			BlockPos otherPos = isSource ? getSourceBlockPos(pos, state) : getNonSourceBlockPos(pos, state);

			BlockState otherState = level.getBlockState(otherPos);
			if(otherState==state.setValue(SOURCE, !isSource)){
				level.setBlock(otherPos, Blocks.AIR.defaultBlockState(), 35);
				level.levelEvent(player, LevelEvent.PARTICLES_DESTROY_BLOCK, otherPos, Block.getId(otherState));
			}
		}

		super.playerWillDestroy(level, pos, state, player);
	}

	@Override public void onRemove(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos,
	                               @NotNull BlockState newState, boolean moving){
		if(state.getValue(SOURCE)&&!(state.is(newState.getBlock())&&newState.getValue(SOURCE))){
			if(level.getBlockEntity(pos) instanceof FishPreparationTableBlockEntity be){
				A.dropContents(level, pos, be.inventory());
				level.updateNeighbourForOutputSignal(pos, this);
			}
			super.onRemove(state, level, pos, newState, moving);
		}
	}

	@Override @Nullable public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state){
		return state.getValue(SOURCE) ? new FishPreparationTableBlockEntity(pos, state) : null;
	}

	@NotNull private static BlockPos getSourceBlockPos(@NotNull BlockPos pos, @NotNull BlockState state){
		return state.getValue(SOURCE) ? pos : pos.relative(state.getValue(HORIZONTAL_FACING).getClockWise());
	}
	@NotNull private static BlockPos getNonSourceBlockPos(@NotNull BlockPos sourcePos, @NotNull BlockState state){
		return getNonSourceBlockPos(sourcePos, state.getValue(HORIZONTAL_FACING));
	}
	@NotNull private static BlockPos getNonSourceBlockPos(@NotNull BlockPos sourcePos, @NotNull Direction facing){
		return sourcePos.relative(facing.getCounterClockWise());
	}
}