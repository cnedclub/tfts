package datagen;

import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.DatapackBuiltinEntriesProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import tictim.tfts.contents.TFTSRegistries;

import java.util.Set;

import static tictim.tfts.TFTSMod.MODID;

@Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Datagen{
	@SubscribeEvent
	public static void generate(GatherDataEvent event){
		boolean server = event.includeServer();
		DataGenerator gen = event.getGenerator();
		var lookup = event.getLookupProvider();
		ExistingFileHelper efh = event.getExistingFileHelper();

		var blockTags = gen.addProvider(server, new BlockTagGen(gen.getPackOutput(), lookup, MODID, efh));
		gen.addProvider(server, new ItemTagGen(gen.getPackOutput(), lookup, blockTags.contentsGetter(), MODID, efh));

		gen.addProvider(server, new DatapackBuiltinEntriesProvider(gen.getPackOutput(), lookup,
				new RegistrySetBuilder().add(TFTSRegistries.ANGLING_ENTRY_REGISTRY_KEY, new AnglingEntryGen()),
				Set.of(MODID)));
	}
}
