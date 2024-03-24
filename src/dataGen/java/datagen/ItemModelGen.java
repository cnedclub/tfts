package datagen;

import net.minecraft.data.PackOutput;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;
import tictim.tfts.TFTSMod;
import tictim.tfts.contents.item.Bait;
import tictim.tfts.contents.item.Fish;
import tictim.tfts.contents.item.Thing;

public class ItemModelGen extends ItemModelProvider{
	public ItemModelGen(PackOutput output, ExistingFileHelper existingFileHelper){
		super(output, TFTSMod.MODID, existingFileHelper);
	}

	@Override protected void registerModels(){
		basicItem(Thing.TROWEL);
		getBuilder(Thing.TROWEL.registryID().toString())
				.parent(new ModelFile.UncheckedModelFile("item/handheld"))
				.texture("layer0", Thing.TROWEL.registryID().withPrefix("item/"));
		basicItem(Thing.COOKING_MORTAR);

		basicItem(Thing.SMALL_FISH_FILLET);
		basicItem(Thing.COOKED_SMALL_FISH_FILLET);
		basicItem(Thing.FISH_FILLET);
		basicItem(Thing.COOKED_FISH_FILLET);
		basicItem(Thing.STARCH);
		basicItem(Thing.JAJO_COLA);

		basicItem(Fish.BASS);
		basicItem(Fish.CARP);
		basicItem(Fish.CATFISH);
		basicItem(Fish.CREEPER_FISH);
		basicItem(Fish.FLYING_FISH);
		basicItem(Fish.GARIBALDI_DAMSELFISH);
		basicItem(Fish.ROCKFISH);
		basicItem(Fish.TUNA);
		basicItem(Fish.ZOMFISH);
		basicItem(Fish.SEAHORSE);
		basicItem(Fish.MARLIN);
		basicItem(Fish.OARFISH);
		basicItem(Fish.OCEAN_SUNFISH);

		for(var bait : Bait.values()) {
			basicItem(bait);
		}
	}

	// fuck you forge
	private ItemModelBuilder basicItem(ItemLike item){
		return basicItem(item.asItem());
	}
}
