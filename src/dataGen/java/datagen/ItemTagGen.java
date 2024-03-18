package datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tictim.tfts.contents.TFTSItems;
import tictim.tfts.contents.TFTSTags;

import java.util.concurrent.CompletableFuture;

public class ItemTagGen extends ItemTagsProvider{
	public ItemTagGen(PackOutput out,
	                  CompletableFuture<HolderLookup.Provider> lookupProvider,
	                  CompletableFuture<TagLookup<Block>> blockTags,
	                  String modId,
	                  @Nullable ExistingFileHelper existingFileHelper){
		super(out, lookupProvider, blockTags, modId, existingFileHelper);
	}

	@Override protected void addTags(@NotNull HolderLookup.Provider p){
		tag(TFTSTags.TFTS_FISHING_RODS).add(TFTSItems.FISHING_ROD.get());
		tag(TFTSTags.CURIO_BAIT_BOX).add(TFTSItems.BAIT_BOX.get());
	}
}
