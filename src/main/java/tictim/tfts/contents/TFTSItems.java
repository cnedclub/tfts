package tictim.tfts.contents;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.*;
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

	static{
		Fish.register();
		Bait.register();
		Thing.register();
	}

	private static final ResourceLocation mainTab = id("main");

	@SubscribeEvent
	public static void register(RegisterEvent event){
		event.register(Registries.ITEM, new ResourceLocation("fishing_rod"), () -> new TFTSFishingRodItem(p()));

		event.register(Registries.CREATIVE_MODE_TAB, mainTab, () -> CreativeModeTab.builder()
				.title(Component.translatable("item_group."+MODID))
				.icon(() -> new ItemStack(Items.FISHING_ROD))
				.displayItems((p, o) -> {
					o.accept(Items.FISHING_ROD);

					for(Thing thing : Thing.values()) o.accept(thing);
					for(Bait bait : Bait.values()) o.accept(bait);
				})
				.build());

		event.register(Registries.CREATIVE_MODE_TAB, id("fish"), () -> CreativeModeTab.builder()
				.title(Component.translatable("item_group."+MODID+".fish"))
				.icon(() -> new ItemStack(Fish.BASS)).displayItems((p, o) -> {
					for(Fish fish : Fish.values()) o.accept(fish);
				})
				.withTabsBefore(mainTab)
				.build());
	}

	@NotNull private static Item.Properties p(){
		return new Item.Properties();
	}
	@NotNull private static RegistryObject<Item> simple(@NotNull String path){
		return ITEMS.register(path, () -> new Item(p()));
	}
}
