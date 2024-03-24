package datagen;

import net.minecraft.data.loot.packs.VanillaBlockLoot;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;
import tictim.tfts.contents.TFTSBlocks;
import tictim.tfts.contents.TFTSRegistries;

public class BlockLootGen extends VanillaBlockLoot{
	@Override protected void generate(){
		add(TFTSBlocks.FILLET_TABLE.get(), this::createNameableBlockEntityTable);
	}

	@Override @NotNull protected Iterable<Block> getKnownBlocks(){
		return () -> TFTSRegistries.BLOCKS.getEntries().stream()
				.map(RegistryObject::get)
				.iterator();
	}
}
