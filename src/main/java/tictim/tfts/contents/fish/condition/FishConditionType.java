package tictim.tfts.contents.fish.condition;

import com.mojang.serialization.Codec;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public record FishConditionType<T extends FishCondition<T>>(@NotNull Codec<T> codec){
	public FishConditionType{
		Objects.requireNonNull(codec, "codec == null");
	}
}
