package datagen;

import net.minecraft.data.PackOutput;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import tictim.tfts.TFTSMod;
import tictim.tfts.contents.item.Fish;
import tictim.tfts.contents.item.Thing;

public class ItemModelGen extends ItemModelProvider{
	public ItemModelGen(PackOutput output, ExistingFileHelper existingFileHelper){
		super(output, TFTSMod.MODID, existingFileHelper);
	}

	@Override protected void registerModels(){
		basicItem(Thing.COOKING_MORTAR.asItem());
		basicItem(Fish.BASS.asItem());
		basicItem(Fish.CARP.asItem());
		basicItem(Fish.CATFISH.asItem());
		basicItem(Fish.CREEPER_FISH.asItem());
		basicItem(Fish.FLYING_FISH.asItem());
		basicItem(Fish.GARIBALDI_DAMSELFISH.asItem());
		basicItem(Fish.ROCKFISH.asItem());
		basicItem(Fish.TUNA.asItem());
		basicItem(Fish.ZOMFISH.asItem());
	}
}
