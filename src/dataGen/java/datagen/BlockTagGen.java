package datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class BlockTagGen extends BlockTagsProvider{
	public BlockTagGen(PackOutput output,
	                   CompletableFuture<HolderLookup.Provider> lookupProvider,
	                   String modId,
	                   @Nullable ExistingFileHelper existingFileHelper){
		super(output, lookupProvider, modId, existingFileHelper);
	}

	@Override protected void addTags(@NotNull HolderLookup.Provider p){}
}
