package tictim.tfts.contents.item;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tictim.tfts.utils.A;
import tictim.tfts.caps.BaitBoxInventory;
import tictim.tfts.contents.inventory.BaitBoxMenu;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.Objects;
import java.util.function.Supplier;

public class BaitBoxItem extends Item implements ICurioItem, IBaitBoxItem{
	private final int inventorySize;
	private final Supplier<MenuType<BaitBoxMenu>> menuType;

	public BaitBoxItem(int inventorySize, @NotNull Supplier<MenuType<BaitBoxMenu>> menuType, Properties p){
		super(p.stacksTo(1));
		this.inventorySize = inventorySize;
		this.menuType = Objects.requireNonNull(menuType);
	}

	@Override @NotNull
	public InteractionResultHolder<ItemStack> use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand hand){
		ItemStack stack = player.getItemInHand(hand);
		if(!level.isClientSide&&A.get(stack, ForgeCapabilities.ITEM_HANDLER) instanceof IItemHandlerModifiable itemHandler){
			openScreen((ServerPlayer)player, itemHandler, stack,
					hand==InteractionHand.MAIN_HAND ? player.getInventory().selected : Inventory.SLOT_OFFHAND);
		}
		return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
	}

	@Override @Nullable public ICapabilityProvider initCapabilities(@NotNull ItemStack stack, @Nullable CompoundTag tag){
		return new BaitBoxInventory(this.inventorySize);
	}

	@Override public boolean canSync(SlotContext slotContext, ItemStack stack){
		return true;
	}

	@Override @NotNull public CompoundTag writeSyncData(SlotContext ctx, ItemStack stack){
		BaitBoxInventory inv = A.get(stack, BaitBoxInventory.CAP);
		return inv!=null ? inv.serializeNBT() : new CompoundTag();
	}

	@Override public void readSyncData(SlotContext ctx, CompoundTag tag, ItemStack stack){
		BaitBoxInventory inv = A.get(stack, BaitBoxInventory.CAP);
		if(inv!=null) inv.deserializeNBT(tag);
	}

	@Override public void openCurioScreen(@NotNull ServerPlayer player, @NotNull ItemStack stack){
		if(A.get(stack, ForgeCapabilities.ITEM_HANDLER) instanceof IItemHandlerModifiable itemHandler){
			openScreen(player, itemHandler, stack, -1);
		}
	}

	private void openScreen(@NotNull ServerPlayer player, @NotNull IItemHandlerModifiable itemHandler, @NotNull ItemStack stack, int baitBoxIndex){
		NetworkHooks.openScreen(player, new MenuProvider(){
			@Override @NotNull public Component getDisplayName(){
				return stack.getHoverName();
			}
			@Override @NotNull public AbstractContainerMenu createMenu(int cid, @NotNull Inventory inv, @NotNull Player p){
				return new BaitBoxMenu(menuType.get(), cid, inv, itemHandler, stack, baitBoxIndex);
			}
		}, buf -> buf.writeByte(baitBoxIndex));
	}
}
