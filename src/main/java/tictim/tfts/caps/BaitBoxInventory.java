package tictim.tfts.caps;

import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tictim.tfts.contents.TFTSRegistries;
import tictim.tfts.contents.fish.BaitStat;
import tictim.tfts.utils.A;

import java.util.Objects;
import java.util.Optional;

public class BaitBoxInventory implements ICapabilityProvider{
	public static final Capability<BaitBoxInventory> CAP = CapabilityManager.get(new CapabilityToken<>(){});

	private final ItemStack stack;

	private final String[] slotKeys;
	private final int[] tagHashes;
	private final Inv inventory;

	public BaitBoxInventory(@NotNull ItemStack stack, int size){
		this.stack = Objects.requireNonNull(stack, "stack == null");
		this.slotKeys = new String[size];
		for(int i = 0; i<size; i++) this.slotKeys[i] = "Slot"+i;
		this.tagHashes = new int[size];
		this.inventory = new Inv(size);

		for(int i = 0; i<size; i++) checkNBTForUpdate(i);
	}

	@NotNull public IItemHandlerModifiable inventory(){
		return this.inventory;
	}

	public int selectedIndex(){
		CompoundTag tag = this.stack.getTag();
		return tag==null ? 0 : tag.getInt("SelectedIndex");
	}

	public void setSelectedIndex(int selectedIndex){
		selectedIndex = Mth.clamp(selectedIndex, 0, this.inventory.getSlots());
		CompoundTag tag = this.stack.getTag();
		if(tag==null){
			if(selectedIndex==0) return;
			this.stack.setTag(tag = new CompoundTag());
		}else if(tag.getInt("SelectedIndex")==selectedIndex) return;
		tag.putInt("SelectedIndex", selectedIndex);
	}

	private void checkNBTForUpdate(int index){
		CompoundTag tag = getSlotTag(index);
		int hash = Objects.hashCode(tag);
		if(this.tagHashes[index]!=hash){
			this.tagHashes[index] = hash;
			this.inventory.stacks().set(index, tag!=null ? ItemStack.of(tag) : ItemStack.EMPTY);
		}
	}

	@Nullable private CompoundTag getSlotTag(int index){
		CompoundTag tag = this.stack.getTag();
		if(tag==null||!tag.contains(this.slotKeys[index], Tag.TAG_COMPOUND)) return null;
		return tag.getCompound(this.slotKeys[index]);
	}

	private void saveSlot(int index){
		ItemStack stack = this.inventory.stacks().get(index);
		if(stack.isEmpty()){
			CompoundTag tag = this.stack.getTag();
			if(tag==null) return;
			tag.remove(this.slotKeys[index]);
			this.tagHashes[index] = 0;
		}else{
			CompoundTag tag = this.stack.getTag();
			if(tag==null) this.stack.setTag(tag = new CompoundTag());
			CompoundTag itemTag = stack.serializeNBT();
			tag.put(this.slotKeys[index], itemTag);
			this.tagHashes[index] = Objects.hashCode(itemTag);
		}
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

	private final class Inv extends ItemStackHandler{
		private boolean skipUpdate;

		public Inv(int size){
			super(size);
		}

		@NotNull private NonNullList<ItemStack> stacks(){
			return this.stacks;
		}

		@Override
		public void setStackInSlot(int slot, @NotNull ItemStack stack){
			try{
				this.skipUpdate = true;
				super.setStackInSlot(slot, stack);
			}finally{
				this.skipUpdate = false;
			}
		}

		@Override protected void validateSlotIndex(int slot){
			super.validateSlotIndex(slot);
			if(!this.skipUpdate) checkNBTForUpdate(slot);
		}

		@Override protected void onContentsChanged(int slot){
			saveSlot(slot);
		}

		@Override public boolean isItemValid(int slot, @NotNull ItemStack stack){
			RegistryAccess registryAccess = A.getRegistryAccess();
			if(registryAccess==null) return false;
			Optional<Registry<Item>> oItemReg = registryAccess.registry(Registries.ITEM);
			Optional<Registry<BaitStat>> oReg = registryAccess.registry(TFTSRegistries.BAIT_STAT_REGISTRY_KEY);
			if(oItemReg.isEmpty()||oReg.isEmpty()) return false;
			Registry<Item> items = oItemReg.get();
			Registry<BaitStat> baitStats = oReg.get();
			ResourceLocation id = items.getKey(stack.getItem());
			return id!=null&&baitStats.containsKey(id);
		}
	}
}