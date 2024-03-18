package tictim.tfts.utils;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface WgtRoll<T>{
	@NotNull static <T> WgtRoll<T> simple(){
		return new SimpleWgtRoll<>();
	}
	@NotNull static WgtRoll<@NotNull BlockPos> priorityCutBlockPosRoll(double threshold){
		return new PriorityCutBlockPosRoll(threshold);
	}

	void add(T entry, double weight);

	@Nullable T get(@NotNull RandomSource randomSource);
}
