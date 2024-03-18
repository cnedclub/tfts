package tictim.tfts.utils;

import net.minecraft.core.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class A{ // fuck every single java naming conventions, also fuck capabilities
	private A(){}

	@Nullable
	public static <T> T get(@NotNull ICapabilityProvider provider, @NotNull Capability<T> cap){
		return get(provider, cap, null);
	}

	@Nullable
	public static <T> T get(@NotNull ICapabilityProvider provider, @NotNull Capability<T> cap, @Nullable Direction direction){
		return unwrap(provider.getCapability(cap, direction));
	}

	@SuppressWarnings("DataFlowIssue")
	@Nullable
	public static <T> T unwrap(@NotNull LazyOptional<T> lazyOptional){
		return lazyOptional.orElse(null);
	}
}
