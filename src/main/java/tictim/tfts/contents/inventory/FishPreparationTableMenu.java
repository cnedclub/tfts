package tictim.tfts.contents.inventory;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tictim.tfts.contents.TFTSMenus;

public class FishPreparationTableMenu extends AbstractContainerMenu{
	private final IItemHandlerModifiable fishPreparationTable;

	public FishPreparationTableMenu(int id, @NotNull Inventory inv, @NotNull IItemHandlerModifiable fishPreparationTable){
		this(TFTSMenus.FISH_PREPARATION_TABLE.get(), id, inv, fishPreparationTable);
	}
	public FishPreparationTableMenu(int id, @NotNull Inventory inv){
		this(TFTSMenus.FISH_PREPARATION_TABLE.get(), id, inv, new ItemStackHandler(1));
	}

	protected FishPreparationTableMenu(@Nullable MenuType<?> type, int id, @NotNull Inventory inv,
	                                   @NotNull IItemHandlerModifiable fishPreparationTable){
		super(type, id);
		this.fishPreparationTable = fishPreparationTable;


	}


	@Override @NotNull public ItemStack quickMoveStack(@NotNull Player player, int index){

		return null;
	}
	@Override public boolean stillValid(@NotNull Player player){
		return true;
	}
}
