package tictim.tfts.contents;

import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;
import tictim.tfts.contents.fish.AnglingEntryType;
import tictim.tfts.contents.fish.TrashAnglingEntry;
import tictim.tfts.contents.fish.condition.FishConditionType;
import tictim.tfts.contents.fish.SimpleAnglingEntry;
import tictim.tfts.contents.fish.condition.TimeCondition;

import static tictim.tfts.contents.TFTSRegistries.ANGLING_ENTRY_TYPES;
import static tictim.tfts.contents.TFTSRegistries.FISH_CONDITION_TYPES;

public final class AnglingEntries{
	private AnglingEntries(){}

	public static void init(){}

	public static final RegistryObject<AnglingEntryType<SimpleAnglingEntry>> SIMPLE = anglingEntry("angling", SimpleAnglingEntry.TYPE);
	public static final RegistryObject<AnglingEntryType<TrashAnglingEntry>> TRASH = anglingEntry("trash", TrashAnglingEntry.TYPE);

	static{
		TimeCondition.register();
	}

	@NotNull private static <T extends AnglingEntryType<?>> RegistryObject<T> anglingEntry(@NotNull String name, @NotNull T type){
		return ANGLING_ENTRY_TYPES.register(name, () -> type);
	}

	@NotNull private static <T extends FishConditionType<?>> RegistryObject<T> type(@NotNull String name, @NotNull T type){
		return FISH_CONDITION_TYPES.register(name, () -> type);
	}
}
