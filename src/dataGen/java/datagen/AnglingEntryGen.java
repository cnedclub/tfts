package datagen;

import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import org.jetbrains.annotations.NotNull;
import tictim.tfts.TFTSMod;
import tictim.tfts.contents.TFTSRegistries;
import tictim.tfts.contents.anglingentry.AnglingEntry;
import tictim.tfts.contents.anglingentry.TestAnglingEntry;

public final class AnglingEntryGen implements RegistrySetBuilder.RegistryBootstrap<AnglingEntry<?>>{
	@Override public void run(BootstapContext<AnglingEntry<?>> ctx){
		ctx.register(id("test"), new TestAnglingEntry(10));
	}

	@NotNull
	private static ResourceKey<AnglingEntry<?>> id(@NotNull String path){
		return ResourceKey.create(TFTSRegistries.ANGLING_ENTRY_REGISTRY_KEY, TFTSMod.id(path));
	}
}
