package datagen;

import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraftforge.common.data.DatapackBuiltinEntriesProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import tictim.tfts.contents.TFTSRegistries;
import tictim.tfts.utils.A;

import java.util.List;
import java.util.Set;

import static tictim.tfts.TFTSMod.MODID;

@Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Datagen{
	@SubscribeEvent
	public static void generate(GatherDataEvent event){
		boolean server = event.includeServer();
		boolean client = event.includeClient();
		DataGenerator gen = event.getGenerator();
		var lookup = event.getLookupProvider();
		ExistingFileHelper efh = event.getExistingFileHelper();

		var blockTags = gen.addProvider(server, new BlockTagGen(gen.getPackOutput(), lookup, MODID, efh));
		gen.addProvider(server, new ItemTagGen(gen.getPackOutput(), lookup, blockTags.contentsGetter(), MODID, efh));

		gen.addProvider(server, new DatapackBuiltinEntriesProvider(gen.getPackOutput(), lookup,
				new RegistrySetBuilder()
						.add(TFTSRegistries.ANGLING_ENTRY_REGISTRY_KEY, new AnglingEntryGen())
						.add(TFTSRegistries.BAIT_STAT_REGISTRY_KEY, new BaitStatGen()),
				A.stfu()));

		gen.addProvider(server, new LootTableProvider(gen.getPackOutput(), Set.of(),
				List.of(new LootTableProvider.SubProviderEntry(BlockLootGen::new, LootContextParamSets.BLOCK),
						new LootTableProvider.SubProviderEntry(TrowelLootGen::new, LootContextParamSets.EMPTY))));

		gen.addProvider(server, new RecipeGen(gen.getPackOutput()));

		gen.addProvider(client, new ItemModelGen(gen.getPackOutput(), efh));
	}
}
