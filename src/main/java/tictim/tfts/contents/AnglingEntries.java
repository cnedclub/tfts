package tictim.tfts.contents;

import com.mojang.serialization.Codec;
import net.minecraft.resources.ResourceKey;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.*;
import org.jetbrains.annotations.NotNull;
import tictim.tfts.TFTSMod;
import tictim.tfts.contents.anglingentry.AnglingEntry;
import tictim.tfts.contents.anglingentry.AnglingEntryType;
import tictim.tfts.contents.anglingentry.TestAnglingEntry;

import java.util.function.Supplier;

import static tictim.tfts.TFTSMod.id;
import static tictim.tfts.contents.TFTSRegistries.ANGLING_ENTRY_TYPES;

public final class AnglingEntries{
	private AnglingEntries(){}

	public static void init(){}

	public static final RegistryObject<AnglingEntryType<TestAnglingEntry>> TEST = type("test", TestAnglingEntry.CODEC);

	@NotNull
	private static <T extends AnglingEntry<T>> RegistryObject<AnglingEntryType<T>> type(@NotNull String name, @NotNull Codec<T> codec){
		return ANGLING_ENTRY_TYPES.register(name, () -> new AnglingEntryType<>(codec));
	}
}
