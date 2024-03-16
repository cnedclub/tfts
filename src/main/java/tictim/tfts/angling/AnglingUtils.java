package tictim.tfts.angling;

import it.unimi.dsi.fastutil.longs.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public final class AnglingUtils{
	private AnglingUtils(){}

	@NotNull
	public static AnglingEnvironment getFluidEnvironment(
			@Nullable AnglingEnvironment previousEnvironment,
			@NotNull Player player,
			@NotNull BlockPos origin,
			@Nullable FluidState fluidState){
		if(fluidState!=null){
			FluidAnglingEnvironment fae = previousEnvironment instanceof FluidAnglingEnvironment prev ? prev :
					new FluidAnglingEnvironment(fluidState.getType());
			fae.eval(player, origin);
			return fae;
		}
		return NoEnvironment.get();
	}

	private static final LongComparator comp = (pos1, pos2) -> {
		int y1 = BlockPos.getY(pos1);
		int y2 = BlockPos.getY(pos2);
		// highest Y value first
		// XZ values are ignored on comparison
		return Integer.compare(y2, y1);
	};

	private static final int Y_LIMIT = 15;
	private static final int XZ_LIMIT = 11;

	// fluid searching algo
	public static void traverseFluid(
			@NotNull Level level,
			@NotNull BlockPos origin,
			@NotNull Fluid fluid,
			@NotNull Consumer<BlockPos> action){
		LongPriorityQueue branchQueue = new LongHeapPriorityQueue(comp);
		LongSet xzSet = new LongOpenHashSet();

		final int minY = origin.getY()-Y_LIMIT+1;
		int minX = origin.getX(), maxX = origin.getX();
		int minZ = origin.getZ(), maxZ = origin.getZ();

		branchQueue.enqueue(origin.asLong());
		xzSet.add(xz(origin));

		MutableBlockPos cursor = new MutableBlockPos();

		while(!branchQueue.isEmpty()){
			long l = branchQueue.dequeueLong();
			cursor.set(l);
			// scan column
			while(cursor.getY()>=minY){
				FluidState state = level.getFluidState(cursor);
				if(!state.isSourceOfType(fluid)) break;
				action.accept(cursor);
				cursor.move(0, -1, 0);
			}

			// search for new branches
			for(int i = 0; i<4; i++){
				Direction dir = Direction.from2DDataValue(i);
				cursor.set(l).move(dir);
				switch(dir){
					case NORTH -> { // -z
						if(cursor.getZ()<minZ){
							if(maxZ-cursor.getZ()>XZ_LIMIT) continue;
						}
					}
					case SOUTH -> { // +z
						if(cursor.getZ()>maxZ){
							if(cursor.getZ()-minZ>XZ_LIMIT) continue;
						}
					}
					case WEST -> { // -x
						if(cursor.getX()<minX){
							if(maxX-cursor.getX()>XZ_LIMIT) continue;
						}
					}
					case EAST -> { // +x
						if(cursor.getX()>maxX){
							if(cursor.getX()-minX>XZ_LIMIT) continue;
						}
					}
				}

				long xz = xz(cursor);
				if(xzSet.contains(xz)) continue;

				FluidState state = level.getFluidState(cursor);
				if(!state.isSourceOfType(fluid)){
					state = level.getFluidState(cursor.move(0, -1, 0));
					if(!state.isSourceOfType(fluid)) continue;
				}

				branchQueue.enqueue(cursor.asLong());
				xzSet.add(xz);

				switch(dir){
					case NORTH -> { // -z
						if(cursor.getZ()<minZ) minZ = cursor.getZ();
					}
					case SOUTH -> { // +z
						if(cursor.getZ()>maxZ) maxZ = cursor.getZ();
					}
					case WEST -> { // -x
						if(cursor.getX()<minX) minX = cursor.getX();
					}
					case EAST -> { // +x
						if(cursor.getX()>maxX) maxX = cursor.getX();
					}
				}
			}
		}
	}

	private static long xz(@NotNull BlockPos pos){
		return (long)pos.getX()<<32|Integer.toUnsignedLong(pos.getZ());
	}
}
