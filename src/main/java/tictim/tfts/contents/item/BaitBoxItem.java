package tictim.tfts.contents.item;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BaitBoxItem extends Item{
	private final int inventorySize;

	public BaitBoxItem(int inventorySize, Properties p){
		super(p.stacksTo(1));
		this.inventorySize = inventorySize;
	}

	@Override
	@Nullable
	public ICapabilityProvider initCapabilities(@NotNull ItemStack stack, @Nullable CompoundTag tag){
		return new BaixBoxInventory(this.inventorySize);
	}

	public static final class BaixBoxInventory implements ICapabilitySerializable<CompoundTag>{
		private final ItemStackHandler inventory;

		public BaixBoxInventory(int size){
			this.inventory = new ItemStackHandler(size);
		}

		@NotNull
		public IItemHandlerModifiable getInventory(){
			return this.inventory;
		}

		@Nullable
		private LazyOptional<IItemHandler> invLO;

		@Override
		@NotNull
		public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction direction){
			if(cap==ForgeCapabilities.ITEM_HANDLER){
				return (this.invLO==null ? (this.invLO = LazyOptional.of(() -> this.inventory)) : this.invLO).cast();
			}else return LazyOptional.empty();
		}

		@Override public CompoundTag serializeNBT(){
			CompoundTag tag = this.inventory.serializeNBT();
			tag.remove("Size");
			return tag;
		}
		@Override public void deserializeNBT(CompoundTag tag){
			tag.remove("Size");
			this.inventory.deserializeNBT(tag);
		}
	}
}
