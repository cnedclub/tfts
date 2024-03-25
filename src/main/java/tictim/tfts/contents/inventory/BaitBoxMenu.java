package tictim.tfts.contents.inventory;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.DataSlot;
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
	private final DataSlot inventoryLocked = addDataSlot(DataSlot.standalone());

	@Nullable
	private final ItemStack baitBoxItem;
	private final boolean fromCurioSlot;

	@Nullable
	private Slot baitBoxInventorySlot;


	public BaitBoxMenu(@NotNull MenuType<?> type, int containerID,
	                   @NotNull Inventory inventory, @NotNull IItemHandlerModifiable baitBox,
	                   @NotNull ItemStack baitBoxItem, int baitBoxItemIndex, boolean fromCurioSlot){
		super(type, containerID);
		this.baitBox = baitBox;
		this.baitBoxItemIndex = baitBoxItemIndex;

		this.baitBoxItem = baitBoxItem;
		this.fromCurioSlot = fromCurioSlot;

		registerSlots(inventory);

		if(this.fromCurioSlot){
			this.inventoryLocked.set(inventory.player.fishing==null ? 0 : 1);
		}
	}

	public BaitBoxMenu(@NotNull MenuType<?> type, int containerID,
	                   @NotNull Inventory inventory, int baitBoxSize,
	                   int baitBoxItemIndex){
		super(type, containerID);
		this.baitBox = new ItemStackHandler(baitBoxSize){
			@Override public boolean isItemValid(int slot, @NotNull ItemStack stack){
				return BaitBoxInventory.isValid(stack);
			}
		};
		this.baitBoxItemIndex = baitBoxItemIndex;

		this.baitBoxItem = null;
		this.fromCurioSlot = false; // meaningless on client side

		registerSlots(inventory);
	}

	@Nullable
	public Slot baitBoxInventorySlot(){
		return this.baitBoxInventorySlot;
	}
	public boolean isInventoryLocked(){
		return this.inventoryLocked.get()!=0;
	}
	public int baitBoxSize(){
		return this.baitBox.getSlots();
	}

	private void registerSlots(@NotNull Inventory inventory){
		int xStart = 89-9*this.baitBox.getSlots();

		for(int i = 0; i<this.baitBox.getSlots(); i++){
			addSlot(new SlotItemHandler(this.baitBox, i, xStart+i*18, 29){
				@Override public boolean mayPlace(@NotNull ItemStack stack){
					return !isInventoryLocked()&&super.mayPlace(stack);
				}
				@Override public boolean mayPickup(Player player){
					return !isInventoryLocked()&&super.mayPickup(player);
				}
			});
		}

		A.createInventorySlots(this::addSlot, (i, x, y) -> i==this.baitBoxItemIndex ?
				this.baitBoxInventorySlot = new ViewOnlySlot(inventory, i, x, y) : new Slot(inventory, i, x, y));
	}

	@Override @NotNull public ItemStack quickMoveStack(@NotNull Player player, int index){
		Slot slot = this.slots.get(index);
		if(!slot.hasItem()) return ItemStack.EMPTY;

		ItemStack slotItem = slot.getItem();
		ItemStack result = slotItem.copy();

		int baitBoxSize = this.baitBox.getSlots();
		if(index<baitBoxSize){
			if(!this.moveItemStackTo(slotItem, baitBoxSize, baitBoxSize+36, true)) return ItemStack.EMPTY;
		}else if(!isInventoryLocked()&&BaitBoxInventory.isValid(slotItem)){
			if(!this.moveItemStackTo(slotItem, 0, baitBoxSize, false)) return ItemStack.EMPTY;
		}else if(index<baitBoxSize+27){
			if(!this.moveItemStackTo(slotItem, baitBoxSize+27, baitBoxSize+36, false)) return ItemStack.EMPTY;
		}else{
			if(!this.moveItemStackTo(slotItem, baitBoxSize, baitBoxSize+27, false)) return ItemStack.EMPTY;
		}

		if(slotItem.isEmpty()) slot.setByPlayer(ItemStack.EMPTY);
		else slot.setChanged();

		if(slotItem.getCount()==result.getCount()) return ItemStack.EMPTY;

		slot.onTake(player, slotItem);
		return result;
	}

	@Override public boolean stillValid(@NotNull Player player){
		if(this.fromCurioSlot){
			this.inventoryLocked.set(player.fishing==null ? 0 : 1);
		}
		if(this.baitBoxItem==null) return true;
		if(this.baitBoxItem.isEmpty()) return false;
		if(this.baitBoxItemIndex<0||this.baitBoxItemIndex>=player.getInventory().getContainerSize()) return true;
		ItemStack invItem = player.getInventory().getItem(baitBoxItemIndex);
		return invItem==this.baitBoxItem;
	}
}
