package datagen;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import tictim.tfts.contents.TFTSRegistries;
import tictim.tfts.contents.bait.BaitStat;

import java.util.Map;

public class BaitStatGen implements RegistrySetBuilder.RegistryBootstrap<BaitStat>{
	private final Map<Item, BaitStatBuilder> builders = new Object2ObjectOpenHashMap<>();

	protected void registerAll(){
		register(Items.COBBLESTONE).stat("test/cobblestone", 1);
		register(Items.ACACIA_SLAB).stat("test/acacia_slab", 1);
		register(Items.BEE_SPAWN_EGG).stat("test/spawn_egg/bee", 1);
	}

	@Override public final void run(@NotNull BootstapContext<BaitStat> context){
		registerAll();

		for(var e : builders.entrySet()){
			ResourceLocation id = ForgeRegistries.ITEMS.getKey(e.getKey());
			if(id==null) throw new IllegalStateException("Cannot get ID of item instance "+e.getKey());

			context.register(
					ResourceKey.create(TFTSRegistries.BAIT_STAT_REGISTRY_KEY, id),
					e.getValue().create());
		}
	}

	protected BaitStatBuilder register(@NotNull ItemLike item){
		BaitStatBuilder builder = new BaitStatBuilder();
		if(builders.put(item.asItem(), builder)!=null){
			throw new IllegalStateException("Already registered");
		}
		return builder;
	}
}
