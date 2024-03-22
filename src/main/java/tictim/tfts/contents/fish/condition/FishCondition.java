package tictim.tfts.contents.fish.condition;

import org.jetbrains.annotations.NotNull;
import tictim.tfts.contents.fish.AnglingContext;

public interface FishCondition<T extends FishCondition<T>>{
	FishConditionType<T> type();
	boolean matches(@NotNull AnglingContext context);
}
