package tictim.tfts.contents.fish.condition;

import com.mojang.serialization.Codec;
import org.jetbrains.annotations.NotNull;
import tictim.tfts.contents.TFTSRegistries;
import tictim.tfts.contents.fish.AnglingContext;

import java.util.Locale;

public enum TimeCondition implements FishCondition<TimeCondition>{
	DAY,
	NIGHT;

	private final FishConditionType<TimeCondition> type = new FishConditionType<>(Codec.unit(this));

	@Override public FishConditionType<TimeCondition> type(){
		return this.type;
	}
	@Override public boolean matches(@NotNull AnglingContext context){
		return context.level.isDay()==(this==DAY);
	}

	private static boolean init;

	public static void register(){
		if(init) throw new IllegalStateException("fuck you");
		else init = true;

		for(TimeCondition c : values()){
			TFTSRegistries.FISH_CONDITION_TYPES.register(c.name().toLowerCase(Locale.ROOT), c::type);
		}
	}
}
