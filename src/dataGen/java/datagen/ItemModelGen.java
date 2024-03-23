package datagen;

import net.minecraft.data.PackOutput;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import tictim.tfts.TFTSMod;
import tictim.tfts.contents.item.Thing;

public class ItemModelGen extends ItemModelProvider{
	public ItemModelGen(PackOutput output, ExistingFileHelper existingFileHelper){
		super(output, TFTSMod.MODID, existingFileHelper);
	}

	@Override protected void registerModels(){
		basicItem(Thing.COOKING_MORTAR.asItem());
	}
}
