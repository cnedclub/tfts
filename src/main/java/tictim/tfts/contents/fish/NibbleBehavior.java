package tictim.tfts.contents.fish;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

/**
 * Describes behavior of "nibbling" stages.
 */
public sealed interface NibbleBehavior{
	Codec<NibbleBehavior> CODEC = StringRepresentable.fromEnum(Type::values)
			.dispatch(NibbleBehavior::type, t -> switch(t){
				case NONE -> None.CODEC;
				case SNATCH -> Snatch.CODEC;
				case NIBBLE -> Nibble.CODEC;
			});

	@NotNull static NibbleBehavior.None none(){
		return None.INSTANCE;
	}
	@NotNull static NibbleBehavior.Snatch snatch(){
		return Snatch.INSTANCE;
	}
	@NotNull static NibbleBehavior.Nibble nibble(double biteChance){
		return new Nibble(biteChance);
	}

	@NotNull Type type();

	/**
	 * No nibbling, just "bites" the hook instantly.
	 */
	final class None implements NibbleBehavior{
		private None(){}
		private static final None INSTANCE = new None();
		private static final Codec<None> CODEC = Codec.unit(INSTANCE);

		@Override @NotNull public Type type(){
			return Type.NONE;
		}
		@Override public String toString(){
			return "None";
		}
	}

	/**
	 * Snatch the bait as soon as possible, which immediately triggers the biting.
	 */
	final class Snatch implements NibbleBehavior{
		private Snatch(){}
		private static final Snatch INSTANCE = new Snatch();
		private static final Codec<Snatch> CODEC = Codec.unit(INSTANCE);

		@Override @NotNull public Type type(){
			return Type.SNATCH;
		}
		@Override public String toString(){
			return "Snatch";
		}
	}

	/**
	 * Tries to nibble away the bait used, with a chance to accidentally bite the hook.<br/>
	 * Note that the chances can be modified by other factors.
	 *
	 * @param biteChance Bite chance (0 ~ 1)
	 */
	record Nibble(double biteChance) implements NibbleBehavior{
		private static final Codec<Nibble> CODEC = RecordCodecBuilder.create(b -> b.group(
				Codec.doubleRange(0, 1).fieldOf("bite_chance").forGetter(Nibble::biteChance)
		).apply(b, Nibble::new));

		@Override @NotNull public Type type(){
			return Type.NIBBLE;
		}
	}

	enum Type implements StringRepresentable{
		NONE("none"),
		SNATCH("snatch"),
		NIBBLE("nibble");

		private final String name;

		Type(@NotNull String name){
			this.name = name;
		}

		@Override @NotNull public String getSerializedName(){
			return name;
		}
	}
}
