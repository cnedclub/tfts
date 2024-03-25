package tictim.tfts.contents.fish.condition;

import com.mojang.serialization.Codec;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import tictim.tfts.contents.AbstractEntryType;

public final class FishConditionType<T extends FishCondition<T>> extends AbstractEntryType<T>{
	public FishConditionType(@NotNull ResourceLocation id, @NotNull Codec<T> codec){
		super(id, codec);
	}
}
