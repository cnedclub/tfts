package datagen;

import net.minecraft.data.PackOutput;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;
import tictim.tfts.TFTSMod;
import tictim.tfts.contents.item.Thing;

public class ItemModelGen extends ItemModelProvider{
	public ItemModelGen(PackOutput output, ExistingFileHelper existingFileHelper){
		super(output, TFTSMod.MODID, existingFileHelper);
	}

	@Override protected void registerModels(){
		basicItem(Thing.TROWEL.asItem());
		getBuilder(Thing.TROWEL.registryID().toString())
				.parent(new ModelFile.UncheckedModelFile("item/handheld"))
				.texture("layer0", Thing.TROWEL.registryID().withPrefix("item/"));
		basicItem(Thing.COOKING_MORTAR.asItem());
		basicItem(Thing.STARCH.asItem());
		basicItem(Thing.JAJO_COLA.asItem());
	}
}
