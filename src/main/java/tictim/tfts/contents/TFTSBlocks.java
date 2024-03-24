package tictim.tfts.contents;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.registries.RegistryObject;
import tictim.tfts.contents.block.FilletTableBlock;
import tictim.tfts.contents.block.FilletTableBlockEntity;
import tictim.tfts.utils.A;

import static tictim.tfts.contents.TFTSRegistries.BLOCKS;
import static tictim.tfts.contents.TFTSRegistries.BLOCK_ENTITIES;

public final class TFTSBlocks{
	private TFTSBlocks(){}

	public static void init(){}

	public static final RegistryObject<Block> FILLET_TABLE = BLOCKS.register("fillet_table",
			() -> new FilletTableBlock(BlockBehaviour.Properties.of()
					.mapColor(MapColor.WOOD)
					.instrument(NoteBlockInstrument.BASS)
					.strength(2.5f)
					.sound(SoundType.WOOD)));

	public static final RegistryObject<BlockEntityType<FilletTableBlockEntity>> FILLET_TABLE_ENTITY = BLOCK_ENTITIES.register("fillet_table",
					() -> BlockEntityType.Builder.of(FilletTableBlockEntity::new, FILLET_TABLE.get())
							.build(A.stfu()));
}
