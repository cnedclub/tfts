package tictim.tfts.contents;

import com.mojang.serialization.Codec;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public abstract class AbstractEntryType<T>{
	@NotNull private final ResourceLocation id;
	@NotNull private final Codec<T> codec;

	public AbstractEntryType(@NotNull ResourceLocation id, @NotNull Codec<T> codec){
		this.id = Objects.requireNonNull(id, "id == null");
		this.codec = Objects.requireNonNull(codec, "codec == null");
	}

	@NotNull public final ResourceLocation id(){
		return id;
	}
	@NotNull public final Codec<T> codec(){
		return codec;
	}

	@Override public boolean equals(Object o){
		if(this==o) return true;
		if(o==null||getClass()!=o.getClass()) return false;
		return Objects.equals(id, ((AbstractEntryType<?>)o).id);
	}

	@Override public int hashCode(){
		return Objects.hash(id);
	}

	@Override public String toString(){
		return this.getClass().getSimpleName()+"["+this.id+']';
	}
}
