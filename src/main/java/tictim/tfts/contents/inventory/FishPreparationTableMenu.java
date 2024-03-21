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
import tictim.tfts.contents.TFTSRecipes;
import tictim.tfts.contents.block.FishPreparationTableBlockEntity;
import tictim.tfts.contents.recipe.FishPreparationContext;
import tictim.tfts.contents.recipe.FishPreparationRecipe;
import tictim.tfts.contents.recipe.InWorldFishPreparationProcessor;
import tictim.tfts.utils.A;

import java.util.Objects;

public class FishPreparationTableMenu extends AbstractContainerMenu{
	private final Player player;
	private final IItemHandlerModifiable fishPreparationTable;
	@Nullable private final FishPreparationTableBlockEntity blockEntity;
	@Nullable private final MinecraftServer server;

	private final DataSlot shit;

	public FishPreparationTableMenu(int id, @NotNull Inventory inventory, @NotNull FishPreparationTableBlockEntity be){
		this(TFTSMenus.FISH_PREPARATION_TABLE.get(), id, inventory, be.inventory(), be);
	}
	public FishPreparationTableMenu(int id, @NotNull Inventory inventory){
		this(TFTSMenus.FISH_PREPARATION_TABLE.get(), id, inventory, new ItemStackHandler(1), null);
	}

	protected FishPreparationTableMenu(@Nullable MenuType<?> type, int id, @NotNull Inventory inventory,
	                                   @NotNull IItemHandlerModifiable fishPreparationTable,
	                                   @Nullable FishPreparationTableBlockEntity blockEntity){
		super(type, id);
		this.player = inventory.player;
		this.fishPreparationTable = fishPreparationTable;
		this.blockEntity = blockEntity;
		this.server = inventory.player.getServer();
		this.shit = addDataSlot(DataSlot.standalone());

		addSlot(new SlotItemHandler(this.fishPreparationTable, 0, 80, 11));
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
		if(!this.fishPreparationTable.getStackInSlot(0).isEmpty()){
			FishPreparationContext ctx = new FishPreparationContext(this.fishPreparationTable, this.server, this.player, this.blockEntity);
			for(var recipe : this.server.getRecipeManager().getAllRecipesFor(TFTSRecipes.PREPARATION.get())){
				if(recipe.matches(ctx)!=null){
					this.shit.set(1);
					return;
				}
			}
		}
		this.shit.set(0);
	}

	public boolean canProcess(){
		return this.shit.get()!=0;
	}

	public void prepareFish(@NotNull ServerPlayer player){
		if(this.fishPreparationTable.getStackInSlot(0).isEmpty()) return;
		FishPreparationContext ctx = new FishPreparationContext(this.fishPreparationTable, player.server, player, this.blockEntity);
		for(var recipe : player.server.getRecipeManager().getAllRecipesFor(TFTSRecipes.PREPARATION.get())){
			FishPreparationRecipe.Result result = recipe.matches(ctx);
			if(result!=null){
				InWorldFishPreparationProcessor processor = new InWorldFishPreparationProcessor(player.serverLevel(),
						Objects.requireNonNull(ctx.worldPosition()));
				ItemStack remaining = result.process(ctx, processor);
				processor.dropExperience();
				this.fishPreparationTable.setStackInSlot(0, remaining);
				// TODO pseudo item particles on item processing? if possible
				return;
			}
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
