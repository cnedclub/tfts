package tictim.tfts.contents.block;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.Nameable;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tictim.tfts.contents.TFTSBlocks;
import tictim.tfts.contents.inventory.FishPreparationTableMenu;
import tictim.tfts.utils.A;

import static tictim.tfts.TFTSMod.MODID;

public class FishPreparationTableBlockEntity extends BlockEntity implements MenuProvider, Nameable{
	private final ItemStackHandler inventory = new ItemStackHandler(1){
		@Override protected void onContentsChanged(int slot){
			setChanged();
		}
	};

	@Nullable private Component customName;

	protected FishPreparationTableBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state){
		super(type, pos, state);
	}
	public FishPreparationTableBlockEntity(BlockPos pos, BlockState state){
		super(TFTSBlocks.FISH_PREPARATION_TABLE_ENTITY.get(), pos, state);
	}

	@NotNull public ItemStackHandler inventory(){
		return inventory;
	}

	@Override @NotNull public Component getName(){
		return this.customName!=null ? this.customName :
				Component.translatable("container."+MODID+".fish_preparation_table");
	}
	@Override @NotNull public Component getDisplayName(){
		return getName();
	}
	@Override @Nullable public Component getCustomName(){
		return this.customName;
	}
	public void setCustomName(@Nullable Component customName){
		this.customName = customName;
		setChanged();
	}

	@Override @NotNull public AbstractContainerMenu createMenu(int id, @NotNull Inventory inv, @NotNull Player player){
		return new FishPreparationTableMenu(id, inv, this.inventory);
	}

	@Override @Nullable public Packet<ClientGamePacketListener> getUpdatePacket(){
		return ClientboundBlockEntityDataPacket.create(this);
	}
	@Override @NotNull public CompoundTag getUpdateTag(){
		return A.writeWithoutSize(this.inventory);
	}

	@Override public void load(@NotNull CompoundTag tag){
		super.load(tag);
		tag.merge(A.writeWithoutSize(this.inventory));
		if(tag.contains("CustomName", Tag.TAG_STRING)){
			this.customName = Component.Serializer.fromJson(tag.getString("CustomName"));
		}
	}

	@Override protected void saveAdditional(@NotNull CompoundTag tag){
		super.saveAdditional(tag);
		A.readWithoutSize(this.inventory, tag);
		if(this.customName!=null){
			tag.putString("CustomName", Component.Serializer.toJson(this.customName));
		}
	}
}
