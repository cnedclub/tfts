package datagen;

import net.minecraft.data.PackOutput;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import tictim.tfts.TFTSMod;
import tictim.tfts.contents.TFTSBlocks;
import tictim.tfts.contents.block.FilletTableBlock;

public class BlockStateGen extends BlockStateProvider{
	public BlockStateGen(PackOutput output, ExistingFileHelper exFileHelper){
		super(output, TFTSMod.MODID, exFileHelper);
	}

	@Override protected void registerStatesAndModels(){
		horizontalBlock(TFTSBlocks.FILLET_TABLE.get(), state -> this.models()
				.getExistingFile(state.getValue(FilletTableBlock.SOURCE) ?
						TFTSMod.id("block/fillet_table_a") : TFTSMod.id("block/fillet_table_b")
				));
	}
}
