package tictim.tfts.contents.inventory;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tictim.tfts.contents.TFTSMenus;
import tictim.tfts.contents.block.FilletTableBlockEntity;
import tictim.tfts.contents.recipe.FilletContext;
import tictim.tfts.contents.recipe.FilletRecipe;
import tictim.tfts.contents.recipe.InWorldRecipeProcessor;
import tictim.tfts.utils.A;

import java.util.Objects;

public class FilletTableMenu extends AbstractContainerMenu{
	private final Player player;
	private final IItemHandlerModifiable table;
	@Nullable private final FilletTableBlockEntity blockEntity;
	@Nullable private final MinecraftServer server;

	private final DataSlot shit;

	public FilletTableMenu(int id, @NotNull Inventory inventory, @NotNull FilletTableBlockEntity be){
		this(TFTSMenus.FILLET_TABLE.get(), id, inventory, be.inventory(), be);
	}
	public FilletTableMenu(int id, @NotNull Inventory inventory){
		this(TFTSMenus.FILLET_TABLE.get(), id, inventory, new ItemStackHandler(1), null);
	}

	protected FilletTableMenu(@Nullable MenuType<?> type, int id, @NotNull Inventory inventory,
	                          @NotNull IItemHandlerModifiable table,
	                          @Nullable FilletTableBlockEntity blockEntity){
		super(type, id);
		this.player = inventory.player;
		this.table = table;
		this.blockEntity = blockEntity;
		this.server = inventory.player.getServer();
		this.shit = addDataSlot(DataSlot.standalone());

		addSlot(new SlotItemHandler(this.table, 0, 80, 11));
		addSlotListener(new ContainerListener(){
			@Override public void slotChanged(@NotNull AbstractContainerMenu m, int i, @NotNull ItemStack stack){
				if(i==0) updateButtonState();
			}
			@Override public void dataChanged(@NotNull AbstractContainerMenu m, int i, int v){}
		});
		A.createInventorySlots(this::addSlot, inventory);

		updateButtonState();
	}

	private void updateButtonState(){
		if(this.server==null||this.blockEntity==null) return;
		if(!this.table.getStackInSlot(0).isEmpty()){
			FilletContext ctx = new FilletContext(this.table, this.server, this.player, this.blockEntity);
			if(A.getRecipe(this.server, FilletRecipe.TYPE, ctx)!=null){
				this.shit.set(1);
				return;
			}
		}
		this.shit.set(0);
	}

	public boolean canProcess(){
		return this.shit.get()!=0;
	}

	public void doFillet(@NotNull ServerPlayer player){
		if(this.table.getStackInSlot(0).isEmpty()) return;
		FilletContext ctx = new FilletContext(this.table, player.server, player, this.blockEntity);
		var result = A.getRecipe(player.server, FilletRecipe.TYPE, ctx);
		if(result!=null){
			InWorldRecipeProcessor processor = new InWorldRecipeProcessor(player.serverLevel(),
					Objects.requireNonNull(ctx.worldPosition()));
			ItemStack remaining = result.process(ctx, processor);
			processor.dropExperience();
			this.table.setStackInSlot(0, remaining);
			// TODO pseudo item particles on item processing? if possible
		}
	}

	@Override @NotNull public ItemStack quickMoveStack(@NotNull Player player, int index){
		Slot slot = this.slots.get(index);
		if(!slot.hasItem()) return ItemStack.EMPTY;

		ItemStack slotItem = slot.getItem();
		ItemStack result = slotItem.copy();

		if(index<1){
			if(!this.moveItemStackTo(slotItem, 1, 1+36, true)) return ItemStack.EMPTY;
		}else{
			if(!this.moveItemStackTo(slotItem, 0, 1, false)) return ItemStack.EMPTY;
		}

		if(slotItem.isEmpty()) slot.setByPlayer(ItemStack.EMPTY);
		else slot.setChanged();

		if(slotItem.getCount()==result.getCount()) return ItemStack.EMPTY;

		slot.onTake(player, slotItem);
		return result;
	}

	@Override public boolean stillValid(@NotNull Player player){
		return this.blockEntity==null||Container.stillValidBlockEntity(this.blockEntity, player);
	}
}
