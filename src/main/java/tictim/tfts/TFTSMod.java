package tictim.tfts;

import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import tictim.tfts.client.TFTSOverlay;
import tictim.tfts.contents.TFTSMenus;
import tictim.tfts.contents.TFTSRegistries;
import tictim.tfts.contents.entity.TFTSHook;
import tictim.tfts.contents.inventory.BaitBoxInventory;
import tictim.tfts.net.TFTSNet;

@Mod(TFTSMod.MODID)
public class TFTSMod{
	public static final String MODID = "tfts";
	public static final Logger LOGGER = LogManager.getLogger("Tales from the Sea");

	public TFTSMod(){
		IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
		TFTSRegistries.init(bus);
		bus.register(TFTSMod.class);

		TFTSNet.init();
	}

	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)
	public static void clientSetup(FMLClientSetupEvent event){
		event.enqueueWork(() -> {
			ItemProperties.register(Items.FISHING_ROD, new ResourceLocation("cast"),
					(stack, level, e, i) -> e instanceof Player p&&
							p.fishing instanceof TFTSHook hook&&
							hook.isItemPointingToThisHook(stack) ? 1 : 0);

			TFTSMenus.registerScreens();
		});
	}

	@SubscribeEvent
	public static void registerCapabilities(RegisterCapabilitiesEvent event){
		event.register(BaitBoxInventory.class);
	}

	@SubscribeEvent
	public static void registerGuiOverlays(RegisterGuiOverlaysEvent event){
		event.registerAboveAll(MODID, new TFTSOverlay());
	}

	@NotNull public static ResourceLocation id(@NotNull String path){
		return new ResourceLocation(MODID, path);
	}
}
