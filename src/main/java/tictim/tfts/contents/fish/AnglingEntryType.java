package tictim.tfts.contents.fish;

import com.mojang.serialization.Codec;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public record AnglingEntryType<T extends AnglingEntry<T>>(@NotNull Codec<T> codec){
	public AnglingEntryType{
		Objects.requireNonNull(codec, "codec == null");
	}
}
