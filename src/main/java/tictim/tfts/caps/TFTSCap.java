package tictim.tfts.caps;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class TFTSCap implements ICapabilitySerializable<CompoundTag>{
	public static final Capability<TFTSCap> CAP = CapabilityManager.get(new CapabilityToken<>(){});

	@Nullable
	private LazyOptional<TFTSCap> self;

	@Override public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side){
		if(cap==CAP){
			return (self==null ? (self = LazyOptional.of(() -> this)) : self).cast();
		}else return LazyOptional.empty();
	}

	@Override public CompoundTag serializeNBT(){
		var tag = new CompoundTag();
		// TODO
		return tag;
	}

	@Override public void deserializeNBT(CompoundTag tag){
		// TODO
	}
}
