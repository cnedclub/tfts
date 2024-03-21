package tictim.tfts.contents.inventory;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tictim.tfts.utils.A;

public class BaitBoxMenu extends AbstractContainerMenu{
	private final IItemHandlerModifiable baitBox;
	private final int baitBoxItemIndex;

	@Nullable
	private final ItemStack baitBoxItem;

	@Nullable
	private Slot baitBoxSlot;

	public BaitBoxMenu(@NotNull MenuType<?> type, int containerID,
	                   @NotNull Inventory inventory, @NotNull IItemHandlerModifiable baitBox,
	                   @NotNull ItemStack baitBoxItem, int baitBoxItemIndex){
		super(type, containerID);
		this.baitBox = baitBox;
		this.baitBoxItemIndex = baitBoxItemIndex;

		this.baitBoxItem = baitBoxItem;

		registerSlots(inventory);
	}

	public BaitBoxMenu(@NotNull MenuType<?> type, int containerID,
	                   @NotNull Inventory inventory, int baitBoxSize,
	                   int baitBoxItemIndex){
		super(type, containerID);
		this.baitBox = new ItemStackHandler(baitBoxSize);
		this.baitBoxItemIndex = baitBoxItemIndex;

		this.baitBoxItem = null;

		registerSlots(inventory);
	}

	@Nullable
	public Slot baitBoxSlot(){
		return baitBoxSlot;
	}

	private void registerSlots(@NotNull Inventory inventory){
		int xStart = 89-9*this.baitBox.getSlots();

		for(int i = 0; i<this.baitBox.getSlots(); i++){
			addSlot(new SlotItemHandler(this.baitBox, i, xStart+i*18, 29));
		}

		A.createInventorySlots(this::addSlot, (i, x, y) -> i==this.baitBoxItemIndex ?
				this.baitBoxSlot = new ViewOnlySlot(inventory, i, x, y) : new Slot(inventory, i, x, y));
	}

	@Override @NotNull public ItemStack quickMoveStack(@NotNull Player player, int index){
		Slot slot = this.slots.get(index);
		if(!slot.hasItem()) return ItemStack.EMPTY;

		ItemStack slotItem = slot.getItem();
		ItemStack result = slotItem.copy();

		int baitBoxSize = this.baitBox.getSlots();
		if(index<baitBoxSize){
			if(!this.moveItemStackTo(slotItem, baitBoxSize, baitBoxSize+36, true)) return ItemStack.EMPTY;
		}else{
			if(!this.moveItemStackTo(slotItem, 0, baitBoxSize, false)) return ItemStack.EMPTY;
		}

		if(slotItem.isEmpty()) slot.setByPlayer(ItemStack.EMPTY);
		else slot.setChanged();

		if(slotItem.getCount()==result.getCount()) return ItemStack.EMPTY;

		slot.onTake(player, slotItem);
		return result;
	}

	@Override public boolean stillValid(@NotNull Player player){
		if(this.baitBoxItem==null) return true;
		if(this.baitBoxItem.isEmpty()) return false;
		if(this.baitBoxItemIndex<0||this.baitBoxItemIndex>=player.getInventory().getContainerSize()) return true;
		ItemStack invItem = player.getInventory().getItem(baitBoxItemIndex);
		return invItem==this.baitBoxItem;
	}
}
