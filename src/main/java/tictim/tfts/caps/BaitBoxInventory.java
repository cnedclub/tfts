package tictim.tfts.caps;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tictim.tfts.utils.A;

public class BaitBoxInventory implements ICapabilitySerializable<CompoundTag>{
	public static final Capability<BaitBoxInventory> CAP = CapabilityManager.get(new CapabilityToken<>(){});

	private final ItemStackHandler inventory;
	private int selectedIndex;

	public BaitBoxInventory(int size){
		this.inventory = new ItemStackHandler(size);
	}

	@NotNull public IItemHandlerModifiable getInventory(){
		return this.inventory;
	}

	public int selectedIndex(){
		return this.selectedIndex;
	}
	public void setSelectedIndex(int selectedIndex){
		this.selectedIndex = Mth.clamp(selectedIndex, 0, this.inventory.getSlots());
	}

	@Nullable private LazyOptional<BaitBoxInventory> self;
	@Nullable private LazyOptional<IItemHandler> invLO;

	@Override @NotNull public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction direction){
		if(cap==CAP){
			return (this.self==null ? (this.self = LazyOptional.of(() -> this)) : this.self).cast();
		}else if(cap==ForgeCapabilities.ITEM_HANDLER){
			return (this.invLO==null ? (this.invLO = LazyOptional.of(() -> this.inventory)) : this.invLO).cast();
		}else return LazyOptional.empty();
	}

	@Override public CompoundTag serializeNBT(){
		CompoundTag tag = A.writeWithoutSize(this.inventory);
		tag.remove("Size");
		if(this.selectedIndex!=0) tag.putInt("SelectedIndex", this.selectedIndex);
		return tag;
	}
	@Override public void deserializeNBT(CompoundTag tag){
		A.readWithoutSize(this.inventory, tag);
		this.selectedIndex = tag.getInt("SelectedIndex");
	}
}