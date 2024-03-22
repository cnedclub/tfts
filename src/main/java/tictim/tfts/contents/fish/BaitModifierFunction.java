package tictim.tfts.contents.fish;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;
import java.util.Locale;

public sealed interface BaitModifierFunction{
	@NotNull Type type();
	double getModifier(@NotNull BaitStat baitStat);

	Codec<Type> TYPE_CODEC = StringRepresentable.fromEnum(Type::values);
	Codec<BaitModifierFunction> CODEC = TYPE_CODEC.dispatch(BaitModifierFunction::type, Type::codec);

	double NO_RESULT = Double.NaN;

	static boolean isValidResult(double result){
		return !Double.isNaN(result);
	}

	record Condition(
			@NotNull BaitModifierCondition condition,
			double modifier
	) implements BaitModifierFunction{
		@Override @NotNull public Type type(){
			return Type.CONDITION;
		}
		@Override public double getModifier(@NotNull BaitStat baitStat){
			return this.condition.matches(baitStat) ? this.modifier : NO_RESULT;
		}
	}

	record FirstMatch(
			@NotNull @Unmodifiable List<BaitModifierFunction> functions
	) implements BaitModifierFunction{
		@Override @NotNull public Type type(){
			return Type.FIRST_MATCH;
		}
		@Override public double getModifier(@NotNull BaitStat baitStat){
			for(BaitModifierFunction f : functions){
				double modifier = f.getModifier(baitStat);
				if(isValidResult(modifier)) return modifier;
			}
			return NO_RESULT;
		}
	}

	record Sum(
			@NotNull @Unmodifiable List<BaitModifierFunction> functions
	) implements BaitModifierFunction{
		@Override @NotNull public Type type(){
			return Type.SUM;
		}
		@Override public double getModifier(@NotNull BaitStat baitStat){
			double sum = NO_RESULT;
			for(BaitModifierFunction f : functions){
				double modifier = f.getModifier(baitStat);
				if(isValidResult(modifier)){
					if(!isValidResult(sum)) sum = modifier;
					else sum += modifier;
				}
			}
			return sum;
		}
	}

	record Min(
			@NotNull @Unmodifiable List<BaitModifierFunction> functions
	) implements BaitModifierFunction{
		@Override @NotNull public Type type(){
			return Type.MIN;
		}
		@Override public double getModifier(@NotNull BaitStat baitStat){
			double min = NO_RESULT;
			for(BaitModifierFunction f : functions){
				double modifier = f.getModifier(baitStat);
				if(isValidResult(modifier)){
					if(!isValidResult(min)) min = modifier;
					else min = Math.min(min, modifier);
				}
			}
			return min;
		}
	}

	record Max(
			@NotNull @Unmodifiable List<BaitModifierFunction> functions
	) implements BaitModifierFunction{
		@Override @NotNull public Type type(){
			return Type.MAX;
		}
		@Override public double getModifier(@NotNull BaitStat baitStat){
			double max = NO_RESULT;
			for(BaitModifierFunction f : functions){
				double modifier = f.getModifier(baitStat);
				if(isValidResult(modifier)){
					if(!isValidResult(max)) max = modifier;
					else max = Math.max(max, modifier);
				}
			}
			return max;
		}
	}

	enum Type implements StringRepresentable{
		CONDITION{
			@Override protected Codec<? extends BaitModifierFunction> createCodec(){
				return RecordCodecBuilder.<Condition>create(b -> b.group(
						BaitModifierCondition.CODEC.fieldOf("condition").forGetter(Condition::condition),
						Codec.DOUBLE.fieldOf("modifier").forGetter(Condition::modifier)
				).apply(b, Condition::new));
			}
		},
		FIRST_MATCH{
			@Override protected Codec<? extends BaitModifierFunction> createCodec(){
				return RecordCodecBuilder.<FirstMatch>create(b -> b.group(
						BaitModifierFunction.CODEC.listOf().fieldOf("functions").forGetter(FirstMatch::functions)
				).apply(b, FirstMatch::new));
			}
		},
		SUM{
			@Override protected Codec<? extends BaitModifierFunction> createCodec(){
				return RecordCodecBuilder.<Sum>create(b -> b.group(
						BaitModifierFunction.CODEC.listOf().fieldOf("functions").forGetter(Sum::functions)
				).apply(b, Sum::new));
			}
		},
		MIN{
			@Override protected Codec<? extends BaitModifierFunction> createCodec(){
				return RecordCodecBuilder.<Min>create(b -> b.group(
						BaitModifierFunction.CODEC.listOf().fieldOf("functions").forGetter(Min::functions)
				).apply(b, Min::new));
			}
		},
		MAX{
			@Override protected Codec<? extends BaitModifierFunction> createCodec(){
				return RecordCodecBuilder.<Max>create(b -> b.group(
						BaitModifierFunction.CODEC.listOf().fieldOf("functions").forGetter(Max::functions)
				).apply(b, Max::new));
			}
		};

		public final String name = name().toLowerCase(Locale.ROOT);
		private Codec<? extends BaitModifierFunction> codec;

		public Codec<? extends BaitModifierFunction> codec(){
			if(this.codec==null) this.codec = createCodec();
			return this.codec;
		}

		@Override @NotNull public String getSerializedName(){
			return this.name;
		}

		protected abstract Codec<? extends BaitModifierFunction> createCodec();
	}
}
