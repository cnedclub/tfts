package tictim.tfts.contents.recipe;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public final class NoContainer implements Container{
	private NoContainer(){}

	private static final NoContainer instance = new NoContainer();

	@NotNull public static NoContainer get(){
		return instance;
	}

	@Override public int getContainerSize(){
		return 0;
	}
	@Override public boolean isEmpty(){
		return true;
	}
	@Override @NotNull public ItemStack getItem(int pSlot){
		return ItemStack.EMPTY;
	}
	@Override @NotNull public ItemStack removeItem(int pSlot, int pAmount){
		return ItemStack.EMPTY;
	}
	@Override @NotNull public ItemStack removeItemNoUpdate(int pSlot){
		return ItemStack.EMPTY;
	}
	@Override public void setItem(int pSlot, @NotNull ItemStack stack){}
	@Override public void setChanged(){}
	@Override public boolean stillValid(@NotNull Player player){
		return true;
	}
	@Override public void clearContent(){}

	@Override public String toString(){
		return "NoContainer";
	}
}
