package datagen.fish;

import org.jetbrains.annotations.NotNull;
import tictim.tfts.contents.fish.BaitModifierCondition;
import tictim.tfts.contents.fish.BaitModifierFunction;

import java.util.List;

public final class FishUtils{
	private FishUtils(){}

	public static BaitModifierFunction.Condition c(@NotNull BaitModifierCondition condition, double modifier){
		return new BaitModifierFunction.Condition(condition, modifier);
	}

	public static BaitModifierFunction.FirstMatch firstMatch(@NotNull BaitModifierFunction... functions){
		checkEmpty(functions);
		return new BaitModifierFunction.FirstMatch(List.of(functions));
	}

	public static BaitModifierFunction.Sum sum(@NotNull BaitModifierFunction... functions){
		checkEmpty(functions);
		return new BaitModifierFunction.Sum(List.of(functions));
	}

	public static BaitModifierFunction.Min min(@NotNull BaitModifierFunction... functions){
		checkEmpty(functions);
		return new BaitModifierFunction.Min(List.of(functions));
	}

	public static BaitModifierFunction.Max max(@NotNull BaitModifierFunction... functions){
		checkEmpty(functions);
		return new BaitModifierFunction.Max(List.of(functions));
	}

	public static BaitModifierCondition.Const t(){
		return BaitModifierCondition.TRUE;
	}
	public static BaitModifierCondition.Const f(){
		return BaitModifierCondition.FALSE;
	}

	public static BaitModifierCondition.RecordCodecCondition.Not not(@NotNull BaitModifierCondition condition){
		return new BaitModifierCondition.RecordCodecCondition.Not(condition);
	}

	public static BaitModifierCondition.RecordCodecCondition.Any any(@NotNull BaitModifierCondition... conditions){
		checkEmpty(conditions);
		return new BaitModifierCondition.RecordCodecCondition.Any(List.of(conditions));
	}

	public static BaitModifierCondition.RecordCodecCondition.All all(@NotNull BaitModifierCondition... conditions){
		checkEmpty(conditions);
		return new BaitModifierCondition.RecordCodecCondition.All(List.of(conditions));
	}

	private static <T> void checkEmpty(@NotNull T[] array){
		if(array.length==0) throw new IllegalArgumentException("Did you forget to add parameters?");
	}
}
