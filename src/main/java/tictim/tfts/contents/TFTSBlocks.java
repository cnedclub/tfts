package tictim.tfts.contents;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.registries.RegistryObject;
import tictim.tfts.contents.block.FishPreparationTableBlock;
import tictim.tfts.contents.block.FishPreparationTableBlockEntity;
import tictim.tfts.utils.A;

import static tictim.tfts.contents.TFTSRegistries.BLOCKS;
import static tictim.tfts.contents.TFTSRegistries.BLOCK_ENTITIES;

public final class TFTSBlocks{
	private TFTSBlocks(){}

	public static void init(){}

	public static final RegistryObject<Block> FISH_PREPARATION_TABLE = BLOCKS.register("fish_preparation_table",
			() -> new FishPreparationTableBlock(BlockBehaviour.Properties.of()
					.mapColor(MapColor.WOOD)
					.instrument(NoteBlockInstrument.BASS)
					.strength(2.5f)
					.sound(SoundType.WOOD)));

	public static final RegistryObject<BlockEntityType<FishPreparationTableBlockEntity>> FISH_PREPARATION_TABLE_ENTITY = BLOCK_ENTITIES.register("fish_preparation_table",
					() -> BlockEntityType.Builder.of(FishPreparationTableBlockEntity::new, FISH_PREPARATION_TABLE.get())
							.build(A.stfu()));
}
