package datagen;

import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;
import tictim.tfts.TFTSMod;
import tictim.tfts.angling.NibbleBehavior;
import tictim.tfts.contents.TFTSRegistries;
import tictim.tfts.contents.anglingentry.AnglingEntry;
import tictim.tfts.contents.anglingentry.TestAnglingEntry;

import java.util.List;

public final class AnglingEntryGen implements RegistrySetBuilder.RegistryBootstrap<AnglingEntry<?>>{
	@Override public void run(BootstapContext<AnglingEntry<?>> ctx){
		ctx.register(id("test1"), new TestAnglingEntry(
				10,
				List.of(new ItemStack(Items.COBBLESTONE)),
				NibbleBehavior.none(),
				0,
				false));
		ctx.register(id("test2"), new TestAnglingEntry(
				10,
				List.of(new ItemStack(Items.ACACIA_SLAB)),
				NibbleBehavior.snatch(),
				.1,
				true));
		ctx.register(id("test3"), new TestAnglingEntry(
				10,
				List.of(new ItemStack(Items.BEE_SPAWN_EGG)),
				NibbleBehavior.nibble(0.5),
				.1,
				true));
	}

	@NotNull
	private static ResourceKey<AnglingEntry<?>> id(@NotNull String path){
		return ResourceKey.create(TFTSRegistries.ANGLING_ENTRY_REGISTRY_KEY, TFTSMod.id(path));
	}
}
