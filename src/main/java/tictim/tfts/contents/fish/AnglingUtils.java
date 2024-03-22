package tictim.tfts.contents.fish;

import it.unimi.dsi.fastutil.longs.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tictim.tfts.TFTSMod;
import tictim.tfts.caps.BaitBoxInventory;
import tictim.tfts.contents.TFTSRegistries;
import tictim.tfts.contents.TFTSTags;
import tictim.tfts.utils.A;
import tictim.tfts.utils.WgtRoll;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotResult;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;

import java.util.Optional;
import java.util.function.Consumer;

public final class AnglingUtils{
	private AnglingUtils(){}

	public static final String CURIO_BAIT_BOX_ID = "tfts_bait_box";

	@NotNull
	public static AnglingEnvironment getFluidEnvironment(
			@Nullable AnglingEnvironment previousEnvironment,
			@NotNull ServerLevel level,
			@NotNull BlockPos origin,
			@Nullable FluidState fluidState){
		if(fluidState!=null){
			FluidAnglingEnvironment fae = previousEnvironment instanceof FluidAnglingEnvironment prev ? prev :
					new FluidAnglingEnvironment(fluidState.getType());
			fae.eval(level, origin);
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

	public static void traverseFluid(
			@NotNull Level level,
			@NotNull BlockPos origin,
			@Nullable Fluid fluid,
			@NotNull Consumer<BlockPos> action){
		traverseFluid(level, origin, fluid, action, 15, 11);
	}

	public static void traverseFluid(
			@NotNull Level level,
			@NotNull BlockPos origin,
			@Nullable Fluid fluid,
			@NotNull Consumer<BlockPos> action,
			int yLimit,
			int xzLimit){
		LongPriorityQueue branchQueue = new LongHeapPriorityQueue(comp);
		LongSet xzSet = new LongOpenHashSet();

		final int minY = origin.getY()-yLimit+1;
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
				if(fluid!=null ? !state.isSourceOfType(fluid) : state.isEmpty()) break;
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
							if(maxZ-cursor.getZ()>xzLimit) continue;
						}
					}
					case SOUTH -> { // +z
						if(cursor.getZ()>maxZ){
							if(cursor.getZ()-minZ>xzLimit) continue;
						}
					}
					case WEST -> { // -x
						if(cursor.getX()<minX){
							if(maxX-cursor.getX()>xzLimit) continue;
						}
					}
					case EAST -> { // +x
						if(cursor.getX()>maxX){
							if(cursor.getX()-minX>xzLimit) continue;
						}
					}
				}

				long xz = xz(cursor);
				if(xzSet.contains(xz)) continue;

				FluidState state = level.getFluidState(cursor);
				if(fluid!=null ? !state.isSourceOfType(fluid) : state.isEmpty()){
					state = level.getFluidState(cursor.move(0, -1, 0));
					if(fluid!=null ? !state.isSourceOfType(fluid) : state.isEmpty()) continue;
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

	@Nullable
	public static BaitBoxInventory getBaitBoxInventory(@NotNull Player player){
		ICuriosItemHandler curiosItemHandler = A.unwrap(CuriosApi.getCuriosInventory(player));
		if(curiosItemHandler==null) return null;
		for(SlotResult slot : curiosItemHandler.findCurios(CURIO_BAIT_BOX_ID)){
			ItemStack stack = slot.stack();
			if(stack==null||stack.isEmpty()) continue;
			BaitBoxInventory baitBoxInventory = A.get(stack, BaitBoxInventory.CAP);
			if(baitBoxInventory!=null) return baitBoxInventory;
		}
		return null;
	}

	@Nullable
	public static AnglingEntry<?> pick(@NotNull AnglingContext context, @NotNull RandomSource randomSource){
		Optional<Registry<AnglingEntry<?>>> optionalEntries = context.level.getServer().registryAccess()
				.registry(TFTSRegistries.ANGLING_ENTRY_REGISTRY_KEY);
		if(optionalEntries.isEmpty()) return null;

		WgtRoll<AnglingEntry<?>> roll = WgtRoll.simple();
		for(AnglingEntry<?> e : optionalEntries.get()){
			double weight = e.getWeight(context);
			roll.add(e, weight);
		}
		TFTSMod.LOGGER.info(roll);
		return roll.get(randomSource);
	}

	@Nullable
	public static InteractionHand getFishingHand(@NotNull Player player){
		if(isTFTSFishingRod(player.getMainHandItem())) return InteractionHand.MAIN_HAND;
		if(isTFTSFishingRod(player.getOffhandItem())) return InteractionHand.OFF_HAND;
		return null;
	}

	private static boolean isTFTSFishingRod(@NotNull ItemStack stack){
		return !stack.isEmpty()&&stack.is(TFTSTags.TFTS_FISHING_RODS);
	}

	// this is the reason why kotlin is objectively superior language
	@Nullable public static BaitStat getBaitStat(@NotNull Player player, @NotNull RegistryAccess registryAccess){
		BaitBoxInventory baitBoxInv = AnglingUtils.getBaitBoxInventory(player);
		if(baitBoxInv==null) return null;
		int i = baitBoxInv.selectedIndex();
		if(i<0||i>=baitBoxInv.getInventory().getSlots()) return null;
		ItemStack stack = baitBoxInv.getInventory().getStackInSlot(i);
		if(stack.isEmpty()) return null;
		ResourceLocation key = ForgeRegistries.ITEMS.getKey(stack.getItem());
		if(key==null) return null;
		Optional<Registry<BaitStat>> o = registryAccess.registry(TFTSRegistries.BAIT_STAT_REGISTRY_KEY);
		if(o.isEmpty()) return null;
		Registry<BaitStat> reg = o.get();
		return reg.get(key);
	}

	public static boolean consumeBait(@NotNull MinecraftServer server, @NotNull Player player){
		BaitBoxInventory baitBoxInv = AnglingUtils.getBaitBoxInventory(player);
		if(baitBoxInv==null) return false;
		int i = baitBoxInv.selectedIndex();
		if(i<0||i>=baitBoxInv.getInventory().getSlots()) return false;
		ItemStack stack = baitBoxInv.getInventory().getStackInSlot(i);
		if(stack.isEmpty()) return false;
		ResourceLocation key = ForgeRegistries.ITEMS.getKey(stack.getItem());
		if(key==null) return false;
		Optional<Registry<BaitStat>> o = server.registryAccess().registry(TFTSRegistries.BAIT_STAT_REGISTRY_KEY);
		if(o.isEmpty()) return false;
		Registry<BaitStat> reg = o.get();
		if(reg.get(key)==null) return false;
		stack.shrink(1);
		return true;
	}
}
