package tictim.tfts.contents.fish.condition;

import com.mojang.serialization.Codec;
import org.jetbrains.annotations.NotNull;
import tictim.tfts.TFTSMod;
import tictim.tfts.contents.fish.AnglingContext;

import java.util.Locale;
import java.util.function.Consumer;

public enum TimeCondition implements FishCondition<TimeCondition>{
	DAY,
	NIGHT;

	private final FishConditionType<TimeCondition> type;

	TimeCondition(){
		this.type = new FishConditionType<>(TFTSMod.id(name().toLowerCase(Locale.ROOT)), Codec.unit(this));
	}

	@Override public FishConditionType<TimeCondition> type(){
		return this.type;
	}
	@Override public boolean matches(@NotNull AnglingContext context){
		return context.level.isDay()==(this==DAY);
	}

	public static void init(Consumer<@NotNull FishConditionType<?>> register){
		for(TimeCondition c : values()){
			register.accept(c.type);
		}
	}
}
