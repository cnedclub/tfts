package tictim.tfts.contents.fish;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;
import java.util.Locale;
import java.util.function.Function;

public sealed interface BaitModifierCondition permits BaitModifierCondition.Const, BaitModifierCondition.RecordCodecCondition, BaitType{
	boolean matches(@Nullable BaitStat baitStat);

	Const TRUE = new Const(true);
	Const FALSE = new Const(false);
	RecordCodecCondition.NoBait NO_BAIT = new RecordCodecCondition.NoBait();

	Codec<BaitModifierCondition> CODEC = Codec.either(
					Codec.either(
							BaitType.CODEC,
							Codec.BOOL.xmap(b -> b ? TRUE : FALSE, c -> c.value)
					),
					StringRepresentable.fromEnum(RecordCodecCondition.Type::values)
							.<RecordCodecCondition>dispatch(
									RecordCodecCondition::type,
									RecordCodecCondition.Type::codec))
			.xmap(e -> e.map(e2 -> e2.map(Function.identity(), Function.identity()), Function.identity()),
					c -> {
						if(c instanceof BaitType bait) return Either.left(Either.left(bait));
						if(c instanceof Const c_) return Either.left(Either.right(c_));
						if(c instanceof RecordCodecCondition r) return Either.right(r);
						throw new IllegalStateException("Unknown type '"+c+'\'');
					});

	final class Const implements BaitModifierCondition{
		private final boolean value;

		private Const(boolean value){
			this.value = value;
		}

		@Override public boolean matches(@Nullable BaitStat baitStat){
			return this.value;
		}
		@Override public String toString(){
			return this.value+"";
		}
	}

	sealed interface RecordCodecCondition extends BaitModifierCondition{
		@NotNull Type type();

		record Not(@NotNull BaitModifierCondition condition) implements RecordCodecCondition{
			@Override @NotNull public Type type(){
				return Type.NOT;
			}
			@Override public boolean matches(@Nullable BaitStat baitStat){
				return !this.condition.matches(baitStat);
			}
		}

		record Any(@NotNull @Unmodifiable List<BaitModifierCondition> conditions) implements RecordCodecCondition{
			@Override @NotNull public Type type(){
				return Type.ANY;
			}
			@Override public boolean matches(@Nullable BaitStat baitStat){
				for(BaitModifierCondition c : this.conditions){
					if(c.matches(baitStat)) return true;
				}
				return false;
			}
		}

		record All(@NotNull @Unmodifiable List<BaitModifierCondition> conditions) implements RecordCodecCondition{
			@Override @NotNull public Type type(){
				return Type.ALL;
			}
			@Override public boolean matches(@Nullable BaitStat baitStat){
				for(BaitModifierCondition c : this.conditions){
					if(!c.matches(baitStat)) return false;
				}
				return true;
			}
		}

		final class NoBait implements RecordCodecCondition{
			private NoBait(){}


			@Override public boolean matches(@Nullable BaitStat baitStat){
				return baitStat==null;
			}
			@Override @NotNull public Type type(){
				return Type.NO_BAIT;
			}

			@Override public String toString(){
				return "NoBait";
			}
		}

		enum Type implements StringRepresentable{
			NOT{
				@Override protected Codec<? extends RecordCodecCondition> createCodec(){
					return RecordCodecBuilder.<Not>create(b -> b.group(
							BaitModifierCondition.CODEC.fieldOf("condition").forGetter(Not::condition)
					).apply(b, Not::new));
				}
			},
			ANY{
				@Override protected Codec<? extends RecordCodecCondition> createCodec(){
					return RecordCodecBuilder.<Any>create(b -> b.group(
							BaitModifierCondition.CODEC.listOf().fieldOf("conditions").forGetter(Any::conditions)
					).apply(b, Any::new));
				}
			},
			ALL{
				@Override protected Codec<? extends RecordCodecCondition> createCodec(){
					return RecordCodecBuilder.<All>create(b -> b.group(
							BaitModifierCondition.CODEC.listOf().fieldOf("conditions").forGetter(All::conditions)
					).apply(b, All::new));
				}
			},
			NO_BAIT{
				@Override protected Codec<? extends RecordCodecCondition> createCodec(){
					return Codec.<NoBait>unit(() -> BaitModifierCondition.NO_BAIT);
				}
			};

			private final String name = name().toLowerCase(Locale.ROOT);
			private Codec<? extends RecordCodecCondition> codec;

			public Codec<? extends RecordCodecCondition> codec(){
				if(this.codec==null) this.codec = createCodec();
				return this.codec;
			}

			@Override @NotNull public String getSerializedName(){
				return this.name;
			}

			protected abstract Codec<? extends RecordCodecCondition> createCodec();
		}
	}
}
