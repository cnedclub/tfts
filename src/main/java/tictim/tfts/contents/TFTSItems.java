package tictim.tfts.contents;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.RegisterEvent;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;
import tictim.tfts.contents.item.*;

import static tictim.tfts.TFTSMod.MODID;
import static tictim.tfts.TFTSMod.id;
import static tictim.tfts.contents.TFTSRegistries.ITEMS;

@Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class TFTSItems{
	private TFTSItems(){}

	public static void init(){}

	public static final RegistryObject<TFTSFishingRodItem> FISHING_ROD = ITEMS.register("fishing_rod",
			() -> new TFTSFishingRodItem(p()));
	public static final RegistryObject<BaitBoxItem> BAIT_BOX = ITEMS.register("bait_box",
			() -> new BaitBoxItem(3, TFTSMenus.BAIT_BOX, p()));

	public static final RegistryObject<BlockItem> FISH_PREPARATION_TABLE = ITEMS.register("fish_preparation_table",
			() -> new BlockItem(TFTSBlocks.FISH_PREPARATION_TABLE.get(), p()));

	public static final RegistryObject<TrowelItem> TROWEL = ITEMS.register("trowel",
			() -> new TrowelItem(p()));

	static{
		Fish.register();
		Bait.register();
	}

	@SubscribeEvent
	public static void registerCreativeModeTab(RegisterEvent event){
		event.register(Registries.CREATIVE_MODE_TAB, id("main"), () -> CreativeModeTab.builder()
				.title(Component.translatable("item_group."+MODID))
				.icon(() -> new ItemStack(FISHING_ROD.get()))
				.displayItems((p, o) -> {
					o.accept(FISHING_ROD.get());
					o.accept(BAIT_BOX.get());
					o.accept(TROWEL.get());

					o.accept(FISH_PREPARATION_TABLE.get());
				})
				.build());
	}

	@NotNull private static Item.Properties p(){
		return new Item.Properties();
	}
	@NotNull private static RegistryObject<Item> simple(@NotNull String path){
		return ITEMS.register(path, () -> new Item(p()));
	}
}
