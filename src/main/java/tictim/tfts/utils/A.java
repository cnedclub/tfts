package tictim.tfts.utils;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public final class A{ // fuck every single java naming conventions, also fuck capabilities
	private A(){}

	/**
	 * Fuck lazyoptionals
	 *
	 * @param provider Fuck lazyoptionals
	 * @param cap      Fuck lazyoptionals
	 * @param <T>      Fuck lazyoptionals
	 * @return Fuck lazyoptionals
	 * @see #get(ICapabilityProvider, Capability, Direction)
	 */
	@Nullable
	public static <T> T get(@NotNull ICapabilityProvider provider, @NotNull Capability<T> cap){
		return get(provider, cap, null);
	}

	/**
	 * Fuck lazyoptionals
	 *
	 * @param provider  Fuck lazyoptionals
	 * @param cap       Fuck lazyoptionals
	 * @param direction Fuck lazyoptionals
	 * @param <T>       Fuck lazyoptionals
	 * @return Fuck lazyoptionals
	 */
	@Nullable
	public static <T> T get(@NotNull ICapabilityProvider provider, @NotNull Capability<T> cap, @Nullable Direction direction){
		return unwrap(provider.getCapability(cap, direction));
	}

	/**
	 * Fuck lazyoptionals
	 *
	 * @param lazyOptional Fuck lazyoptionals
	 * @param <T>          Fuck lazyoptionals
	 * @return Fuck lazyoptionals
	 */
	@SuppressWarnings("DataFlowIssue")
	@Nullable
	public static <T> T unwrap(@NotNull LazyOptional<T> lazyOptional){
		return lazyOptional.orElse(null);
	}

	/**
	 * Makes static analysis warnings about fake non-null context shut the fuck up
	 *
	 * @param <T> Meaningless
	 * @return Null
	 */
	@SuppressWarnings("DataFlowIssue")
	@NotNull public static <T> T stfu(){
		return null;
	}

	@NotNull
	public static CompoundTag writeWithoutSize(@NotNull ItemStackHandler itemStackHandler){
		CompoundTag tag = itemStackHandler.serializeNBT();
		if(tag.getList("Items", Tag.TAG_COMPOUND).isEmpty()) tag.remove("Items");
		tag.remove("Size");
		return tag;
	}

	public static void readWithoutSize(@NotNull ItemStackHandler itemStackHandler, @NotNull CompoundTag tag){
		tag.remove("Size");
		itemStackHandler.deserializeNBT(tag);
	}

	public static void dropContents(@NotNull Level level, @NotNull BlockPos pos, @NotNull IItemHandler itemHandler){
		for(int i = 0; i<itemHandler.getSlots(); i++){
			Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), itemHandler.getStackInSlot(i));
		}
	}

	public static void createInventorySlots(@NotNull Consumer<Slot> register, @NotNull Inventory inventory){
		createInventorySlots(76, register, inventory);
	}
	public static void createInventorySlots(int invYOffset, @NotNull Consumer<Slot> register, @NotNull Inventory inventory){
		createInventorySlots(invYOffset, register, (i, x, y) -> new Slot(inventory, i, x, y));
	}
	public static void createInventorySlots(@NotNull Consumer<Slot> register, @NotNull SlotFactory slotFactory){
		createInventorySlots(76, register, slotFactory);
	}
	public static void createInventorySlots(int invYOffset, @NotNull Consumer<Slot> register, @NotNull SlotFactory slotFactory){
		for(int i = 0; i<3; ++i){
			for(int j = 0; j<9; ++j){
				register.accept(slotFactory.create(j+i*9+9, 8+j*18, invYOffset+8+i*18));
			}
		}

		for(int i = 0; i<9; ++i){
			register.accept(slotFactory.create(i, 8+i*18, invYOffset+66));
		}
	}

	@FunctionalInterface
	public interface SlotFactory{
		@NotNull Slot create(int index, int x, int y);
	}
}
