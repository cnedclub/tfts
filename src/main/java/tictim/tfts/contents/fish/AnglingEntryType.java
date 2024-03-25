package tictim.tfts.contents.fish;

import com.mojang.serialization.Codec;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import tictim.tfts.contents.AbstractEntryType;

public final class AnglingEntryType<T extends AnglingEntry<T>> extends AbstractEntryType<T>{
	public AnglingEntryType(@NotNull ResourceLocation id, @NotNull Codec<T> codec){
		super(id, codec);
	}
}
